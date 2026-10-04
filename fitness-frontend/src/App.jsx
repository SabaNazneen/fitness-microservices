import { Button } from "@mui/material";
import { useContext, useEffect } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { BrowserRouter } from "react-router";
import { setCredentials } from "./store/authSlice";
import { useState } from "react";

function App() {
  const{token,tokenData,logIn,logOut,isAuthenticated}
    =useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady,setAuthReady]=useState(false);

  useEffect(()=>{
    if(token)
    {
      dispatch(setCredentials({token,user:tokenData}));
      setAuthReady(true);
    }
  },[token,tokenData,dispatch])
  return (
    <BrowserRouter>
      <Button variant="contained"
      onClick={()=>{logIn();}}>
      LOGIN
      </Button>
    </BrowserRouter>
  );
}

export default App;