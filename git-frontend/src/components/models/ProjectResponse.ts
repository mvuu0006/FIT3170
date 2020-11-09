export interface ProjectResponse {
  users: User[]
  projects: Project
}

export interface Project {
  projectName: string | null
  projectId: string
  projectUnitCode: string | null
  projectYear: string | null
  projectSemester: string | null
}

export interface User {
  givenName: string | null
  familyName: string | null
  emailAddress: string | null
  userGroup: string | null
  projects: Project[]
}

