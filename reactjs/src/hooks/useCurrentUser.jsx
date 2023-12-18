import {useContext} from 'react';
import { CurrentUserContext, blankUser } from "../CurrentUserContext";

const useCurrentUser = () => {

    const [currentUser, setCurrentUser] = useContext(CurrentUserContext);

    function onLogin(newUserData) {
        localStorage.setItem('currentUser', JSON.stringify(newUserData));
        setCurrentUser(newUserData);
    }

    function onLogout() {
        localStorage.removeItem("currentUser");
        setCurrentUser(blankUser);
    }

    const hasAdmin = () => hasRole('ADMIN');

    function hasRole(roleName) {
        return currentUser.roles.find(role => role === roleName) !== undefined;
    }

    return {
        currentUser,
        onLogin,
        onLogout,
        hasAdmin,
    }
};

export default useCurrentUser;