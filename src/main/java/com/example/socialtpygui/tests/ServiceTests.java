package com.example.socialtpygui.tests;


import com.example.socialtpygui.domain.*;
import com.example.socialtpygui.repository.db.FriendshipDb;
import com.example.socialtpygui.repository.db.MessageDb;
import com.example.socialtpygui.repository.db.FriendshipRequestDb;
import com.example.socialtpygui.repository.db.UserDb;
import com.example.socialtpygui.service.SuperService;
import com.example.socialtpygui.service.entityservice.*;
import com.example.socialtpygui.service.validators.MessageValidator;
import com.example.socialtpygui.service.validators.NonExistingException;
import com.example.socialtpygui.service.validators.UserValidator;
import com.example.socialtpygui.service.validators.ValidationException;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;

public class ServiceTests {
    private static UserValidator userValidator = new UserValidator();
    private static MessageValidator messageValidator = new MessageValidator(userValidator);

    private static FriendshipRequestDb friendshipRequestDb = new FriendshipRequestDb("jdbc:postgresql://localhost:5432/SocialNetworkTest", "postgres", "postgres");
    private static UserDb userDb = new UserDb("jdbc:postgresql://localhost:5432/SocialNetworkTest", "postgres", "postgres");
    private static FriendshipDb friendshipDb = new FriendshipDb("jdbc:postgresql://localhost:5432/SocialNetworkTest", "postgres", "postgres");
    private static MessageDb messageDb = new MessageDb("jdbc:postgresql://localhost:5432/SocialNetworkTest", "postgres", "postgres");
    private static UserService userService = new UserService(userDb, userValidator);
    private static NetworkService networkService = new NetworkService(userDb, friendshipDb);
    private static MessageService messageService = new MessageService(messageDb);
    private static FriendshipService friendshipService = new FriendshipService(friendshipRequestDb, friendshipDb);
    private static SuperService service = new SuperService(messageService, networkService, friendshipService, userService, userValidator, messageValidator);


    private ServiceTests() {
    }

    public static void runTests() {
        testAddUser();
        testRemoveUser();
        testUsers();
        testAddFriend();
        testRemoveFriend();
        testNrCommunities();
        testSocialCommunity();
        testGetFriends();
        testGetFriendsSince();
        testGetAllConversation();
        testGetMessages();
        testSaveDelete();
        testSendRequest();
        testAcceptRequest();
        testDeclineRequest();
        testGetRequests();
        testReplayAll();
        testgetUsersByName();
        testfriendshipDate();
        testfriendshipRequestDate();
        testfriendRequest();
    }

    private static void testAddUser() {

        try {
            User newUser = new User("", "", "", "");
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }
        try {
            User newUser = new User("", "", "", null);
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        try {
            User newUser = new User("", "", null, "");
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        try {
            User newUser = new User("", "", "user@mail.", "");
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }


        try {
            User newUser = new User("", "", "user@mail.com", "");
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        try {
            User newUser = new User("fName", "", "user@mail.com", "");
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        User newUserM = new User("fName", "lName", "user@mail.com", "a");
        service.addUser(newUserM);


        try {
            User newUser = new User("fName", "lName", "user@mail.com", "a");
            service.addUser(newUser);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

    }

    private static void testRemoveUser() {
        try {
            service.removeUser("");
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }


        try {
            service.removeUser("user@mail.");
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        try {
            service.removeUser("user1@mail.com");
            assert false;
        } catch (NonExistingException exception) {
            assert true;
        }

        try {
            service.removeUser("user@mail.com");
            assert true;
        } catch (NonExistingException exception) {
            assert false;
        }
    }

    private static void testUsers() {
        Iterable<User> users = service.users();
        long size = StreamSupport.stream(users.spliterator(), false).count();
        assert (size == 6);
        assert (users.iterator().next().getId().equals("jon1@yahoo.com"));
    }

    private static void testAddFriend() {
        List<String> emails = new ArrayList();
        emails.add("gg@gmail.com");
        try {
            service.addFriend("user@mail.com", emails);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        emails.clear();
        emails.add("user1@gmail.");
        try {
            service.addFriend("gg@gmail.com", emails);
            assert false;
        } catch (NonExistingException exception) {
            assert true;
        }

        emails.add("gg@gmail.com");
        try {
            service.addFriend("gg@gmail.com", emails);
            assert false;
        } catch (NonExistingException exception) {
            assert true;
        }

        emails.clear();
        emails.add("jon1@yahoo.com");
        try {
            service.addFriend("gg@gmail.com", emails);
            assert false;
        } catch (NonExistingException exception) {
            assert true;
        }

        emails.clear();
        emails.add("snj@gmail.com");
        try {
            service.addFriend("gg@gmail.com", emails);
            assert true;
        } catch (NonExistingException exception) {
            assert false;
        }
    }

    private static void testRemoveFriend() {
        List<String> emails = new ArrayList();
        emails.add("snj@gmail.com");
        try {
            service.removeFriend("gg@gmail.com", emails);
            assert true;
        } catch (NonExistingException exception) {
            assert false;
        }

        emails.clear();
        emails.add("gg@gmail.com");
        try {
            service.removeFriend("user@mail.com", emails);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        emails.clear();
        emails.add("user1@gmail.");
        try {
            service.removeFriend("gg@gmail.com", emails);
            assert false;
        } catch (NonExistingException exception) {
            assert true;
        }

        emails.add("gg@gmail.com");
        try {
            service.removeFriend("gg@gmail.com", emails);
            assert false;
        } catch (NonExistingException exception) {
            assert true;
        }

        emails.clear();
        emails.add("jon1@yahoo.com");
        try {
            service.removeFriend("gg@gmail.com", emails);
            assert true;
        } catch (NonExistingException exception) {
            assert false;
        }

        service.addFriend("gg@gmail.com", emails);
    }

    private static void testNrCommunities() {
        int nrCommunities = service.nrCommunities();
        assert (nrCommunities == 3);
    }

    private static void testSocialCommunity() {
        Iterable<User> socialCommunity = service.socialCommunity();
        long size = StreamSupport.stream(socialCommunity.spliterator(), false).count();
        assert (size == 3);
    }

    private static void testGetFriends() {
        Iterable<FriendShipDTO> friends = service.getFriends("andr@gamail.com");
        long size = StreamSupport.stream(friends.spliterator(), false).count();
        assert (size == 2);

        try {
            Iterable<FriendShipDTO> friends1 = service.getFriends("andr@gamail.c");
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        try {
            Iterable<FriendShipDTO> friends2 = service.getFriends("user@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }
    }

    private static void testGetFriendsSince() {
        try {
            YearMonth date = YearMonth.parse("2020-10", DateTimeFormatter.ofPattern("yyyy-MM"));
            Iterable<FriendShipDTO> friends = service.getFriendsSince("andr@sad.", date);
            assert false;
        } catch (ValidationException exception) {
            assert true;
        }

        /*try {
            YearMonth date= YearMonth.parse("2020-18", DateTimeFormatter.ofPattern("yyyy-MM"));
            Iterable<FriendShipDTO> friends = service.getFriendsSince("andr@sad.", date);
            assert false;
        }catch (ValidationException exception){
            assert true;
        }*/

        YearMonth date = YearMonth.parse("2021-10", DateTimeFormatter.ofPattern("yyyy-MM"));
        Iterable<FriendShipDTO> friends = service.getFriendsSince("andr@gamail.com", date);
        long size = StreamSupport.stream(friends.spliterator(), false).count();
        assert (size == 2);

        try {
            service.getFriendsSince("andrei@gamail.com", date);
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

    }


    private static void testGetAllConversation() {
        List<String> list = service.getAllConversation("gg@gmail.com");
        assert (list.size() == 3);
        assert (list.get(0).equals("jon1@yahoo.com"));
        assert (list.get(1).equals("snj@gmail.com"));
        assert (list.get(2).equals("aand@hotmail.com"));
        try {
            list = service.getAllConversation("dsa");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }
    }

    private static void testGetMessages() {
        List<ReplyMessage> replyMessageList = service.getMessages("aand@hotmail.com", "snj@gmail.com");
        List<Integer> idList = new ArrayList<>();
        for (ReplyMessage replyMessage : replyMessageList) idList.add(replyMessage.getId());
        assert (replyMessageList.size() == 3);
        assert (idList.contains(4));
        assert (idList.contains(6));
        assert (idList.contains(7));
        idList.clear();
        for (ReplyMessage replyMessage : replyMessageList)
            if (replyMessage.getOriginal() != null) idList.add(replyMessage.getOriginal().getId());
        assert (idList.contains(6));
        assert (idList.contains(4));
    }

    private static void testSaveDelete() {
        assert (messageDb.size() == 7);
        List<String> list = new ArrayList<String>();
        list.add("andr@gamail.com");
        try {
            Message newMessage = new Message("gg@gmail.com", list, "Proiect1.pdf", LocalDate.now());
            service.sendMessage(newMessage);
            assert false;
        } catch (ValidationException e) {
            assert true;
        }
        Message newMessage = new Message("snj@gmail.com", list, "Proiect1.pdf", LocalDate.now());
        service.sendMessage(newMessage);
        String sql = "SELECT id FROM message ORDER BY ID DESC LIMIT 1";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SocialNetworkTest", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int id = resultSet.getInt("id");
            assert (messageDb.size() == 8);
            assert (messageDb.findOne(id) != null);
            messageDb.remove(id);
            assert (messageDb.size() == 7);
            assert (messageDb.findOne(id) == null);

            Message message = new Message("snj@gmail.com", list, "Proiect1.pdf", LocalDate.now());
            ReplyMessageDTO newReplyMessageDTO = new ReplyMessageDTO(message, String.valueOf(1));
            service.replyMessage(newReplyMessageDTO);

            try {
                Message message1 = new Message("gg@gmail.com", list, "Proiect1.pdf", LocalDate.now());
                ReplyMessageDTO newReplyMessageDTO1 = new ReplyMessageDTO(message1, String.valueOf(1));
                service.replyMessage(newReplyMessageDTO1);
                assert false;
            } catch (ValidationException e) {
                assert true;
            }
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            id = resultSet.getInt("id");
            assert (messageDb.size() == 8);
            assert (messageDb.findOne(id) != null);
            messageDb.remove(id);
            assert (messageDb.size() == 7);
            assert (messageDb.findOne(id) == null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.clear();
        list.add("gg@gmail.com");
        try {
            Message message = new Message("snj@gmail.com", list, "MesajTest", LocalDate.now());
            service.sendMessage(message);
            assert false;
        } catch (ValidationException e) {
            assert true;
        }
        try {
            Message message = new Message("snj@gmail.com", list, "MesajTest", LocalDate.now());
            ReplyMessageDTO newReplyMessageDTO1 = new ReplyMessageDTO(message, String.valueOf(1));
            service.replyMessage(newReplyMessageDTO1);
            assert false;
        } catch (ValidationException e) {
            assert true;
        }
    }

    private static void testSendRequest() {
        try {
            service.sendRequest("", "");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.sendRequest("sdg", "");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.sendRequest("fgj", "sdfg@mail.");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.sendRequest("andr@gmail.com", "snj@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        try {
            service.sendRequest("gg@gmail.com", "jon1@yahoo.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        try {
            service.sendRequest("gc@gmail.com", "snj@gmail.com");
            assert true;
        } catch (ValidationException | NonExistingException e) {
            assert false;
        }

        try {
            service.sendRequest("gc@gmail.com", "snj@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        try {
            service.sendRequest("gc@gmail.com", "bvx@fdg.ro");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        try {
            service.sendRequest("gc@gmail.com", "gg@gmail.com");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }
    }

    private static void testAcceptRequest() {
        try {
            service.acceptRequest("", "");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.acceptRequest("sdg", "");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.acceptRequest("fgj", "sdfg@mail.");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.acceptRequest("gc@gmail.com", "bvx@fdg.ro");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        try {
            service.acceptRequest("andr@gamail.com", "gc@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        List<String> emails = new ArrayList();
        emails.add("jon1@yahoo.com");
        service.removeFriend("gg@gmail.com", emails);

        service.acceptRequest("gg@gmail.com", "jon1@yahoo.com");

        try {
            service.sendRequest("gg@gmail.com", "jon1@yahoo.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

    }

    private static void testDeclineRequest() {
        try {
            service.declineRequest("", "");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.declineRequest("sdg", "");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.declineRequest("fgj", "sdfg@mail.");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        service.declineRequest("gc@gmail.com", "snj@gmail.com");

        try {
            service.declineRequest("gc@gmail.com", "snj@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        try {
            service.declineRequest("gc@gmail.com", "bvx@fdg.ro");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

        List<String> emails = new ArrayList();
        emails.add("jon1@yahoo.com");
        service.removeFriend("gg@gmail.com", emails);

        service.sendRequest("gg@gmail.com", "jon1@yahoo.com");
        service.addFriend("gg@gmail.com", emails);

    }

    private static void testGetRequests() {
        try {
            service.getRequests("");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.getRequests("dfgh@rwe.");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        Iterable<UserDTO> usersDTO = service.getRequests("andr@gamail.com");
        long size = StreamSupport.stream(usersDTO.spliterator(), false).count();
        assert (size == 2);

        try {
            service.getRequests("gc@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }

    }

    private static void testReplayAll() {
        //Test with invalid data.
        Message message = new Message("andr@gamail.com", null, "De ce ne intrebi?", LocalDate.now());
        ReplyMessageDTO replyMessageDTO = new ReplyMessageDTO(message, "87");
        try {
            service.replayAll(replyMessageDTO);
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }
        message.setFrom("ddasda");
        replyMessageDTO = new ReplyMessageDTO(message, "1");
        try {
            service.replayAll(replyMessageDTO);
            assert false;
        } catch (ValidationException e) {
            assert true;
        }
        message.setFrom("andr@gamail.com");
        message.setMessage("");
        replyMessageDTO = new ReplyMessageDTO(message, "1");
        try {
            service.replayAll(replyMessageDTO);
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        //Test with valid data.
        message.setMessage("De ce ne intrebi?");
        replyMessageDTO = new ReplyMessageDTO(message, "1");
        assert (messageService.size() == 7);
        service.replayAll(replyMessageDTO);
        assert (messageService.size() == 8);
        String sql1 = "SELECT id FROM message ORDER BY ID DESC LIMIT 1";
        String sql2 = "select email from message_recipient order by message desc limit 2";
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SocialNetworkTest", "postgres", "postgres");
             PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
             PreparedStatement preparedStatement2 = connection.prepareStatement(sql2)) {
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            resultSet1.next(); int id = resultSet1.getInt("id");
            assert (messageService.findOne(id).getMessage().equals("De ce ne intrebi?"));
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            resultSet2.next(); assert (replyMessageDTO.getResponse().getTo().contains(resultSet2.getString("email")));
            resultSet2.next(); assert (replyMessageDTO.getResponse().getTo().contains(resultSet2.getString("email")));
            messageService.remove(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void testgetUsersByName()
    {
        List<String> list1 = new ArrayList<>();
        service.getUsersByName("a").forEach(elem-> list1.add(elem.getId()));
        assert (list1.size() == 4);
        assert (list1.contains("gc@gmail.com"));
        assert (list1.contains("gg@gmail.com"));
        assert (list1.contains("andr@gamail.com"));
        assert (list1.contains("aand@hotmail.com"));
        list1.clear();
        service.getUsersByName("adasda dasda").forEach(elem->list1.add(elem.getId()));
        assert (list1.size() == 0);
    }


    private static void testfriendshipRequestDate()
    {
        assert (service.friendshipRequestDate("andr@gamail.com", "snj@gmail.com").toString().equals("2021-10-29"));
        assert (service.friendshipRequestDate("andr@gamail.com", "snj@sadgmail.com") == null);
        assert (service.friendshipRequestDate("andr@gamail.com", "aand@hotmail.com").toString().equals("2021-10-29"));

    }

    private static void testfriendshipDate()
    {
        assert (service.friendshipDate("snj@gmail.com", "andr@gamail.com").toString().equals("2021-10-29"));
        assert (service.friendshipDate("snj@gmail.com", "andrrqwe@gamail.com") == null);
        assert (service.friendshipDate("andr@gamail.com", "aand@hotmail.com").toString().equals("2021-10-29"));
    }

    private static void testfriendRequest() {
        try {
            service.getRequests("");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        try {
            service.getRequests("dfgh@rwe.");
            assert false;
        } catch (ValidationException e) {
            assert true;
        }

        Iterable<FriendShipDTO> usersDTO = service.getFriendRequest("andr@gamail.com");
        long size = StreamSupport.stream(usersDTO.spliterator(), false).count();
        assert (size == 2);

        try {
            service.getRequests("gc@gmail.com");
            assert false;
        } catch (NonExistingException e) {
            assert true;
        }
    }
}
