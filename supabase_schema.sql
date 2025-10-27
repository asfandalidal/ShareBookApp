-- Supabase Database Schema for ShareBook App
-- Run these commands in your Supabase SQL Editor

-- Enable Row Level Security
ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY;

-- Create users table
CREATE TABLE IF NOT EXISTS public.users (
    uid TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT,
    location TEXT,
    bio TEXT,
    profile_image_url TEXT,
    local_profile_image_path TEXT,
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW()),
    updated_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())
);

-- Create books table
CREATE TABLE IF NOT EXISTS public.books (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    description TEXT,
    isbn TEXT,
    genre TEXT,
    cover_image_url TEXT,
    local_cover_image_path TEXT,
    owner_uid TEXT NOT NULL REFERENCES public.users(uid) ON DELETE CASCADE,
    is_available BOOLEAN DEFAULT true,
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW()),
    updated_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())
);

-- Create book_requests table
CREATE TABLE IF NOT EXISTS public.book_requests (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id TEXT NOT NULL REFERENCES public.books(id) ON DELETE CASCADE,
    requester_uid TEXT NOT NULL REFERENCES public.users(uid) ON DELETE CASCADE,
    owner_uid TEXT NOT NULL REFERENCES public.users(uid) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'pending', -- pending, approved, rejected, completed
    message TEXT,
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW()),
    updated_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())
);

-- Create reviews table
CREATE TABLE IF NOT EXISTS public.reviews (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id TEXT NOT NULL REFERENCES public.books(id) ON DELETE CASCADE,
    reviewer_uid TEXT NOT NULL REFERENCES public.users(uid) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW()),
    updated_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())
);

-- Row Level Security Policies

-- Users can only see and modify their own data
CREATE POLICY "Users can view own profile" ON public.users
    FOR SELECT USING (auth.uid() = uid);

CREATE POLICY "Users can update own profile" ON public.users
    FOR UPDATE USING (auth.uid() = uid);

CREATE POLICY "Users can insert own profile" ON public.users
    FOR INSERT WITH CHECK (auth.uid() = uid);

-- Books policies
CREATE POLICY "Anyone can view available books" ON public.books
    FOR SELECT USING (is_available = true);

CREATE POLICY "Users can view their own books" ON public.books
    FOR SELECT USING (auth.uid() = owner_uid);

CREATE POLICY "Users can insert their own books" ON public.books
    FOR INSERT WITH CHECK (auth.uid() = owner_uid);

CREATE POLICY "Users can update their own books" ON public.books
    FOR UPDATE USING (auth.uid() = owner_uid);

CREATE POLICY "Users can delete their own books" ON public.books
    FOR DELETE USING (auth.uid() = owner_uid);

-- Book requests policies
CREATE POLICY "Users can view their own requests" ON public.book_requests
    FOR SELECT USING (auth.uid() = requester_uid OR auth.uid() = owner_uid);

CREATE POLICY "Users can create requests" ON public.book_requests
    FOR INSERT WITH CHECK (auth.uid() = requester_uid);

CREATE POLICY "Book owners can update requests" ON public.book_requests
    FOR UPDATE USING (auth.uid() = owner_uid);

-- Reviews policies
CREATE POLICY "Anyone can view reviews" ON public.reviews
    FOR SELECT USING (true);

CREATE POLICY "Users can create reviews" ON public.reviews
    FOR INSERT WITH CHECK (auth.uid() = reviewer_uid);

CREATE POLICY "Users can update their own reviews" ON public.reviews
    FOR UPDATE USING (auth.uid() = reviewer_uid);

CREATE POLICY "Users can delete their own reviews" ON public.reviews
    FOR DELETE USING (auth.uid() = reviewer_uid);

-- Enable RLS on all tables
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.books ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.book_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reviews ENABLE ROW LEVEL SECURITY;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_books_owner_uid ON public.books(owner_uid);
CREATE INDEX IF NOT EXISTS idx_books_is_available ON public.books(is_available);
CREATE INDEX IF NOT EXISTS idx_book_requests_requester_uid ON public.book_requests(requester_uid);
CREATE INDEX IF NOT EXISTS idx_book_requests_owner_uid ON public.book_requests(owner_uid);
CREATE INDEX IF NOT EXISTS idx_reviews_book_id ON public.reviews(book_id);

-- Create storage buckets
INSERT INTO storage.buckets (id, name, public) VALUES 
    ('profile-images', 'profile-images', true),
    ('book-covers', 'book-covers', true)
ON CONFLICT (id) DO NOTHING;

-- Storage policies
CREATE POLICY "Users can upload their own profile images" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'profile-images' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Anyone can view profile images" ON storage.objects
    FOR SELECT USING (bucket_id = 'profile-images');

CREATE POLICY "Users can update their own profile images" ON storage.objects
    FOR UPDATE USING (bucket_id = 'profile-images' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Users can delete their own profile images" ON storage.objects
    FOR DELETE USING (bucket_id = 'profile-images' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Users can upload book covers" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'book-covers');

CREATE POLICY "Anyone can view book covers" ON storage.objects
    FOR SELECT USING (bucket_id = 'book-covers');

CREATE POLICY "Users can update book covers" ON storage.objects
    FOR UPDATE USING (bucket_id = 'book-covers');

CREATE POLICY "Users can delete book covers" ON storage.objects
    FOR DELETE USING (bucket_id = 'book-covers');
