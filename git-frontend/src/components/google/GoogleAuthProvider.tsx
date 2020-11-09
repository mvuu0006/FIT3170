import { useGoogleLogin } from "react-use-googlelogin"
import React, { FunctionComponent } from "react"
import { HookReturnValue } from "react-use-googlelogin/dist/types"

export const GoogleAuthContext = React.createContext({} as HookReturnValue)

const GoogleAuthProvider: FunctionComponent = ({ children }) => {
  const googleAuth = useGoogleLogin({
    clientId: "12178522373-eiukpdtqbjg8cmj0no3tjbmisl3qres2.apps.googleusercontent.com",
    persist: true,
    fetchBasicProfile: true,
    uxMode: "redirect",
    redirectUri: "http://spmd-admin-frontend.s3-website-ap-southeast-2.amazonaws.com"
  })

  return <GoogleAuthContext.Provider value={googleAuth}>{children}</GoogleAuthContext.Provider>
}

export const useGoogleAuth = () => React.useContext(GoogleAuthContext)

export default GoogleAuthProvider
