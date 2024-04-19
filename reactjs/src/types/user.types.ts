
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

export type { UserFormFields, RegistrationRequest, User, PersonalInfo, UserSummary };