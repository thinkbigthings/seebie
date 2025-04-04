
interface PersonalInfo {
    displayName: string;
    notificationsEnabled: boolean;
}

interface User {
    email: string;
    publicId: string;
    roles: string[];
    registrationTime: string;
    personalInfo: PersonalInfo;
    isLoggedIn: boolean;
}

interface UserFormFields {
    email: string;
    displayName: string,
    password: string,
    confirmPassword: string
}

interface RegistrationRequest {
    displayName:string,
    plainTextPassword:string,
    email:string
}

interface PasswordResetRequest {
    plainTextPassword:string,
}

interface UserSummary {
    publicId: string,
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
export type { UserFormFields, RegistrationRequest, PasswordResetRequest, User, PersonalInfo, UserSummary, Action };