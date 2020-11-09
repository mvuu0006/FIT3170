import { AppStatus } from "../models/AppStatus"

export interface ProjectDetail {
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

export interface Admin {
  isAdmin: boolean | null
}

export interface AppState {
  projectDetailStatus: AppStatus
  userDetailStatus: AppStatus
  adminDetailStatus: AppStatus
  currentProject: ProjectDetail | null
  user: User | null
  admin: Admin | null
}

const AppInitialState: AppState = {
  projectDetailStatus: AppStatus.INITIAL,
  userDetailStatus: AppStatus.INITIAL,
  adminDetailStatus: AppStatus.INITIAL,
  currentProject: null,
  user: null,
  admin: null
}

export default AppInitialState
