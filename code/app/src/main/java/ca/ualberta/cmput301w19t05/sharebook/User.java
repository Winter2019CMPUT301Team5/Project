package ca.ualberta.cmput301w19t05.sharebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String userID;
    private String username;
    private String email;
    private Uri image;
    private ArrayList<Book> myBooks;


    public User(String userID, String username, String email, Uri image) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.image = image;
    }

    public User(String userID, String username, String email) {
        this.userID = userID;
        this.username = username;
        this.email = email;
    }

    public User() {

    }

    public Uri getImage() {
        return image;
    }

    public void setUserimage(Uri image) {
        this.image = image;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Book> getShelf() {
        return myBooks;
    }

    public void setShelf(ArrayList<Book> shelf) {
        this.myBooks = shelf;
    }

    public void accept(Book book, Location mLocation){
        book.setStatus("ACCEPTED");
        sendMessage("accept", book.getOwner());
    }
    public void decline(Book book){
        sendMessage("decline", book.getOwner());
    }
    public void sendRequest(Book book){
        book.setStatus("REQUESTED");
        sendMessage("Request", book.getOwner());
        Record record = new Record(book,this,book.getOwner());
    }
    public Notification sendMessage( String message, User receiver){
        Notification notification = new Notification(message,this, receiver);
        return notification;
    }


    
    public void  addShelf(Intent data){
        Book book = (Book) data.getBundleExtra("B").getSerializable("getB");
        myBooks.add(book);
    }

    public void editShelf(Intent data){

    }

    public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in) {
        userID = in.readString();
        username = in.readString();
        email = in.readString();
        image = Uri.parse(in.readString());


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(String.valueOf(image));
    }
}