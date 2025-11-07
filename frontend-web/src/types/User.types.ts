export interface User {
  id: number;
  sub: string;
  username: string;
  profile?: UserProfile;
}

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  profilePicUrl: string;
}

export interface UserResponse {
  id: number;
  sub: string;
  username: string;
  profile?: UserProfile;
}

export interface UserProfilePictureURLResponse {
  profilePicUrl: string;
}

export interface UserSync {
  subject: string;
}

export interface UserProfileRequest {
  userId: number; 
  firstName: String;
  lastName: String;
  profilePicUrl: String;
}

export interface UserProfileResponse {
  userId: number; 
  firstName: String;
  lastName: String;
  profilePicUrl: String;
}
