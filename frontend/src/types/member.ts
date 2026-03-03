export type MemberRole = "USER" | "ADMIN";
export type MemberStatus = "ACTIVE" | "INACTIVE";
export type AuthProvider = "kakao" | "google" | "local";

export interface Member {
    id: number;
    createdDate: string;
    lastModifiedDate: string;

    email?: {
        address: string;
    };

    nickname: string;
    memberRole: MemberRole;
    status: MemberStatus;

    provider: AuthProvider;
    providerId: string;

    active: boolean;
    enabled: boolean;
    username: string;

    authorities: {
        authority: string;
    }[];

    profileImageUrl?: string;
}

export const MemberRoleDisplayName: Record<MemberRole, string> = {
    USER: "일반 회원",
    ADMIN: "관리자",
};

export function getMemberRoleDisplayName(
    role?: MemberRole | null
): string {
    if (!role) return "";
    return MemberRoleDisplayName[role] ?? role;
}
