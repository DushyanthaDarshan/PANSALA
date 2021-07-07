package com.ddr.pansala;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class CommonMethods {

    private static SharedPreferences prefs;

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

//    /**
//     * The method for get current user details from firebase
//     *
//     * @return
//     */
//    protected static UserRole populateCurrentUserDetails(String userId) {
//        rootNode = FirebaseDatabase.getInstance();
//        reference = rootNode.getReference("USER");
//
//        List<UserRole> userRoleList = new ArrayList<>();
//        UserRole user = null;
//        if(userId != null){
//            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
//                            UserRole userRole = dataSnapshot.getValue(UserRole.class);
//                            userRoleList.add(userRole);
//                        }
//                    }
//                }
//            });
//            for (int i = 0; userRoleList.size() > i; i++) {
//                if (userRoleList.get(i).getUserId().equals(userId)) {
//                    user = userRoleList.get(i);
//                }
//            }
//        }
//        return user;
//    }

    protected static void saveSession(Context context, UserRole userRole) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getString("name",null) != null) {
            clearSession(context);
        }
        prefs.edit().putString("name", userRole.getName()).apply();
        prefs.edit().putString("email", userRole.getEmail()).apply();
        prefs.edit().putString("userId", userRole.getUserId()).apply();
        prefs.edit().putString("userType", userRole.getUserType()).apply();
        prefs.edit().putString("userStatus", userRole.getUserStatus()).apply();
    }

    /**
     * Method for get name from current user
     *
     * @return
     */
    protected static String getName() {
        String displayName = null;
        String fullName = prefs.getString("name",null);
        List<String> splitName = Arrays.asList(fullName.split(" "));
        if (splitName.size() != 0) {
            displayName = splitName.get(0);
        }
        return displayName;
    }

    protected static void clearSession(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().apply();
    }
}
