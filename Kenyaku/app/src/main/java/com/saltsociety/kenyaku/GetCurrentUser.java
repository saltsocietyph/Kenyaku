package com.saltsociety.kenyaku;

import com.google.firebase.auth.FirebaseUser;

public class GetCurrentUser {
    private FirebaseUser currentUser;

    public GetCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    // Checks if there's a currently signed-in user
    public boolean isUserNull() {
        boolean isUserNull = true;

        if(currentUser != null) {
            isUserNull = false;
        }

        return isUserNull;
    }
}
