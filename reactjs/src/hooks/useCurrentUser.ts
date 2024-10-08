import {useContext} from 'react';
import {CurrentUserContext, User} from "../utility/CurrentUserContext";
import {blankUser} from "../utility/CurrentUserContext";

const useCurrentUser = () => {

    const [currentUser, setCurrentUser] = useContext(CurrentUserContext);

    function onLogin(newUserData: User) {
        newUserData.isLoggedIn = true;
        localStorage.setItem('currentUser', JSON.stringify(newUserData));
        setCurrentUser(newUserData);
    }

    function onLogout() {
        localStorage.removeItem("currentUser");
        setCurrentUser(blankUser);
    }

    const hasAdmin = () => hasRole('ADMIN');

    function hasRole(roleName: string) {
        return currentUser.roles.some(role => role === roleName);
    }

    return {
        currentUser,
        onLogin,
        onLogout,
        hasAdmin,
    }
};

export default useCurrentUser;