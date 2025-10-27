# Debug Steps - Firebase Books Not Showing

## Immediate Steps:

### 1. Check Firebase Console da Firestore Rules
Go to: https://console.firebase.google.com/project/bookshare-72ba6/firestore/rules

**Paste these rules** (also in FIRESTORE_RULES.txt):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /books/{bookId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update: if request.auth != null && 
                     resource.data.ownerUid == request.auth.uid;
      allow delete: if request.auth != null && 
                     resource.data.ownerUid == request.auth.uid;
    }
  }
}
```

Click **Publish**

### 2. Check if User is Authenticated

In your app:
- Go to Profile screen
- Verify you are logged in
- Try logging out and back in

### 3. Test Adding a Book

Steps:
1. Open AddBookScreen
2. Fill in:
   - Title: "Test Book"
   - Author: "Test Author"
   - Description: "Test Description"
   - Genre: Select any
   - Condition: Select any
   - Category: Select any
3. **Don't add an image** (to simplify)
4. Click "Add Book"
5. Watch for Toast messages

### 4. Check Logcat

Open Android Studio Logcat and filter by:
- `AddBookScreen`
- `FirebaseBook`
- `BookViewModel`

Look for:
- "Adding book: ..."
- "Starting to add book with ID: ..."
- "Saving book to Firestore: ..."
- "Book added successfully with ID: ..."
- Any ERROR messages

### 5. Check Firestore Console

Go to: https://console.firebase.google.com/project/bookshare-72ba6/firestore/data

- Click on "books" collection
- Look for newly created documents
- If documents appear here but not in app, it's a loading issue

### 6. Check Storage Console (if image added)

Go to: https://console.firebase.google.com/project/bookshare-72ba6/storage

- Check "book_covers" folder
- Verify images are uploading

## Common Issues:

### Issue 1: Firestore Rules Too Restrictive
**Solution**: Use the rules above - they allow authenticated users to create books

### Issue 2: User Not Authenticated
**Check**: Open Profile screen, verify you're logged in
**Solution**: Log out and log back in

### Issue 3: Network Issues
**Check**: Ensure device has internet connection
**Solution**: Try again on WiFi

### Issue 4: Firebase Not Initialized
**Check**: Look for errors in Logcat about Firebase initialization
**Solution**: Verify `google-services.json` is in app/ folder

### Issue 5: Collection Not Created
**Check**: Firestore console shows no "books" collection
**Solution**: Manually create a test document in Firestore to trigger collection creation

## Quick Test:

### Add a Test Document Manually in Firestore Console:
1. Go to Firestore console
2. Click "Start collection"
3. Collection ID: `books`
4. Document ID: Leave empty (auto-generated)
5. Add fields:
   - `id`: string → "test-123"
   - `title`: string → "Test Book"
   - `author`: string → "Test Author"
   - `description`: string → "Test"
   - `genre`: string → "Fiction"
   - `coverImageUrl`: string → "https://via.placeholder.com/300"
   - `ownerUid`: string → (your user UID from Firebase Auth)
   - `isAvailable`: boolean → true
   - `createdAt`: number → 1234567890
   - `updatedAt`: number → 1234567890
6. Save
7. Check if this appears in your app's HomeScreen

## Expected Logcat Output:

```
D/AddBookScreen მMain): Adding book: Book(id=, title=Test Book, author=Test Author...)
D/FirebaseBook: Starting to add book with ID: abc-123-def
D/FirebaseBook: No image to upload
D/FirebaseBook: Saving book to Firestore: Book(id=abc-123-def, ...)
D/FirebaseBook: Book added successfully with ID: abc-123-def
D/BookViewModel: Book added successfully with ID: abc-123-def
D/AddBookScreen: Book added successfully
```

If you see errors instead, share the error message for diagnosis.

## Next Steps:

1. ✅ Apply Firestore rules
2. ✅ Try adding a book
3. ✅ Check Logcat
4. ✅ Check Firestore console
5. ✅ Share results

