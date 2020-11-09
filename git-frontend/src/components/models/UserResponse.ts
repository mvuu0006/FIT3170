export interface Project {
  projectName: string | null
  projectId: string
  projectUnitCode: string | null
  projectYear: string | null
  projectSemester: string | null
}

export interface UserResponse {
  firstName: string | null
  lastName: string | null
  emailAddress: string | null
  userGroup: string | null
  projects: Project[]
}
