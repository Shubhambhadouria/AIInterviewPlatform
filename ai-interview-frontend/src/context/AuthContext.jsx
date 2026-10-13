import { useEffect, useState } from "react";
import { getCurrentUser, loginUser, registerUser } from "../api/authApi";
import { getToken, removeToken, saveToken } from "../util/tokenStorage";
import { AuthContext } from "./authState";
export function AuthProvider({children}) {
  const [user,setUser] = useState(null);
  const [initializing,setInitializing] = useState(() => Boolean(getToken()));
  useEffect(() => {
    let active=true;
    if (!getToken()) return () => {active=false;};
    getCurrentUser().then(data => {if(active) setUser(data);})
      .catch(() => {if(active) {removeToken();setUser(null);}})
      .finally(() => {if(active) setInitializing(false);});
    return () => {active=false;};
  },[]);
  async function authenticate(action,payload) {
    const data = await action(payload);
    if (!data.token) throw new Error("Authentication response is missing a token.");
    saveToken(data.token);setUser(data);return data;
  }
  const login = payload => authenticate(loginUser,payload);
  const register = payload => authenticate(registerUser,payload);
  function logout() {removeToken();setUser(null);}
  return <AuthContext.Provider value={{user,initializing,isAuthenticated:Boolean(user),login,register,logout}}>{children}</AuthContext.Provider>;
}
