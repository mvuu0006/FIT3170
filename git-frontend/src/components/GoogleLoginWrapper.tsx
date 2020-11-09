import { FunctionComponent } from "react";
import React from "react";
import { useGoogleAuth } from "./google/GoogleAuthProvider"
import App from "./App"
import Contacts from "./contacts/Contacts";


const GoogleLoginWrapper: FunctionComponent<{page: any}> = ({page}) => {
    const { signIn, googleUser, isInitialized, isSignedIn } = useGoogleAuth()
    const emailAddress = googleUser?.getBasicProfile()?.getEmail()
    const id_token = googleUser?.getAuthResponse().id_token;
    if (id_token !== undefined){
        window.sessionStorage.setItem('google_id_token', id_token);
    }
    if (page !== undefined){
        switch (page) {
            case "git":
                return(
                    <App id_token={id_token}/>
                )
            case "contact":
                return(
                    <Contacts email={emailAddress}/>
                )
        }
    }
    return(<div></div>)
}

export default GoogleLoginWrapper