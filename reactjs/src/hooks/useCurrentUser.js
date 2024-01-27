import {useContext} from 'react';
import { CurrentUserContext } from "../utility/CurrentUserContext";
import {blankUser} from "../utility/Constants";

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