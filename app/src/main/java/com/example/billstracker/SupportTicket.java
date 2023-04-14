package com.example.billstracker;

import java.util.ArrayList;

public class SupportTicket {

    private String name;
    private String userUid;
    private String userEmail;
    private String agent;
    private String agentUid;
    private ArrayList <String> notes;
    private boolean open;
    private String id;
    private int unreadByUser;
    private int unreadByAgent;

    public SupportTicket(String name, String userUid, String userEmail, String agent, ArrayList <String> notes, boolean open, String id, int unreadByUser, int unreadByAgent, String agentUid) {

        setName(name);
        setUserUid(userUid);
        setUserEmail(userEmail);
        setAgent(agent);
        setNotes(notes);
        setOpen(open);
        setId(id);
        setUnreadByUser(unreadByUser);
        setUnreadByAgent(unreadByAgent);
        setAgentUid(agentUid);
    }

    public SupportTicket () {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public ArrayList <String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList <String> notes) {
        this.notes = notes;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUnreadByUser() {
        return unreadByUser;
    }

    public void setUnreadByUser(int unreadByUser) {
        this.unreadByUser = unreadByUser;
    }

    public int getUnreadByAgent() {
        return unreadByAgent;
    }

    public void setUnreadByAgent(int unreadByAgent) {
        this.unreadByAgent = unreadByAgent;
    }

    public String getAgentUid() {
        return agentUid;
    }

    public void setAgentUid(String agentUid) {
        this.agentUid = agentUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
