package com.example.gamestore1;

public class FriendRequest {
    public int id;
    public String fromUser;
    public String toUser;
    public String status;
    public String date;

    public FriendRequest() {}

    public FriendRequest(String fromUser, String toUser, String status) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
    }
}