
interface PersonalInfo {
    displayName: string;
    email: string;
    notificationsEnabled: boolean;
}

interface User {
    username: string;
    roles: string[];
    registrationTime: string;
    personalInfo: PersonalInfo;
    isLoggedIn: boolean;
}

interface UserFormFields {
    username: string,
    email: string,
    password: string,
    confirmPassword: string
}

interface RegistrationRequest {
    username:string,
    plainTextPassword:string,
    email:string
}

interface UserSummary {
    username: string,
    displayName: string
}

enum ActionType {
    LOAD_USER,
    UPDATE_USER
}

type Action =
    | { type: ActionType.LOAD_USER; payload: User }
    | { type: ActionType.UPDATE_USER; payload: Partial<PersonalInfo> };

export { ActionType }
export type { UserFormFields, RegistrationRequest, User, PersonalInfo, UserSummary, Action };