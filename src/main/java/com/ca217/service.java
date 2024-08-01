package com.ca217;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class service {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowedUsersRepository followedUsersRepository;

    @Autowired
    private MessageRepository messageRepository;


    //users


    @Transactional
    public Map<String, Object> signUpUser(String name, String email, String password, String userPic) {
        // Return a JSON-serializable response
        Map<String, Object> response = new HashMap<>();
        response.put("message", userRepository.signUpUser(name, email, password, userPic));
        return response;
    }

    @Transactional
    public String checkUser(String email, String password) {
        return userRepository.checkUser(email, password);
    }

    public Map<String, Object> findAllUsers() {

        // Return a JSON-serializable response
        Map<String, Object> response = new HashMap<>();
        response.put("result", userRepository.findAll());
        return response;
    }

    public Map<String, Object> findUserByEmail(String email) {

        // Return a JSON-serializable response
        Map<String, Object> response = new HashMap<>();
        response.put("result", userRepository.findById(email));
        return response;
    }






    //Functions of Follow

    @Transactional
    public Map<String, Object> followUser(FollowedUsersTable followedUser) {
        Map<String, Object> response = new HashMap<>();

        // Check if the follow relationship already exists
        FollowedUsersId id = new FollowedUsersId(followedUser.getUserEmail(), followedUser.getFollowedUserEmail());
        if (followedUsersRepository.existsById(id)) {
            response.put("message", "Already following " + followedUser.getFollowedUserEmail());
        } else {
            followedUsersRepository.save(followedUser);
            response.put("message", "Successfully followed " + followedUser.getFollowedUserEmail());
        }

        return response;
    }

    @Transactional
    public Map<String, Object> unfollowUser(String userEmail, String followedUserEmail) {
        Map<String, Object> response = new HashMap<>();

        // Check if the follow relationship exists
        FollowedUsersId id = new FollowedUsersId(userEmail, followedUserEmail);
        if (followedUsersRepository.existsById(id)) {
            followedUsersRepository.deleteById(id);
            response.put("message", "Successfully unfollowed " + followedUserEmail);

            // Check if neither user follows the other
            boolean userFollowsOther = followedUsersRepository.existsByUserEmailAndFollowedUserEmail(userEmail, followedUserEmail);
            boolean otherFollowsUser = followedUsersRepository.existsByUserEmailAndFollowedUserEmail(followedUserEmail, userEmail);

            if (!userFollowsOther && !otherFollowsUser) {
                // Neither user follows the other, so delete their messages
                List<Message> messagesToDelete = messageRepository.findBySenderEmailAndReceiverEmail(userEmail, followedUserEmail);
                messagesToDelete.addAll(messageRepository.findByReceiverEmailAndSenderEmail(userEmail, followedUserEmail));
                messageRepository.deleteAll(messagesToDelete);
                response.put("message", response.get("message") + " Messages between " + userEmail + " and " + followedUserEmail + " have been deleted.");
            }

        } else {
            response.put("message", "Not following " + followedUserEmail);
        }

        return response;
    }


    @Transactional
    public Map<String, Object> getFollowedUsers(String email) {
        Map<String, Object> response = new HashMap<>();
        List<FollowedUsersTable> followedUsers = followedUsersRepository.findByUserEmail(email);
        Map<Integer, UsersTable> followedUserDetails = new HashMap<>();

        for (FollowedUsersTable followedUser : followedUsers) {
            Optional<UsersTable> userDetailsOpt = userRepository.findById(followedUser.getFollowedUserEmail());
            userDetailsOpt.ifPresent(user -> followedUserDetails.put(followedUserDetails.values().size(), user));
        }

        response.put("result", followedUserDetails);
        return response;
    }

    @Transactional
    public Map<String, Object> getFollowers(String email) {
        Map<String, Object> response = new HashMap<>();
        // Fetch records where the given email is in the followed_user_email column
        List<FollowedUsersTable> followers = followedUsersRepository.findByFollowedUserEmail(email);

        Map<Integer, UsersTable> followersDetails = new HashMap<>();

        // Retrieve user details for each follower
        for (FollowedUsersTable follower : followers) {
            Optional<UsersTable> userDetailsOpt = userRepository.findById(follower.getUserEmail());
            userDetailsOpt.ifPresent(user -> followersDetails.put(followersDetails.size(), user));
        }

        response.put("result", followersDetails);
        return response;
    }








    //Functions of Message
    @Transactional
    public List<Message> getMessagesBetweenUsers(String SenderEmail, String ReceiverEmai) {
        List<Message> sentMessages = messageRepository.findBySenderEmailAndReceiverEmail(SenderEmail, ReceiverEmai);
        List<Message> receivedMessages = messageRepository.findByReceiverEmailAndSenderEmail(SenderEmail, ReceiverEmai);
        sentMessages.addAll(receivedMessages);
        // Sort the combined list by timestamp
        return sentMessages.stream()
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .collect(Collectors.toList());
    }
    @Transactional
    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

}

