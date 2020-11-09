import { ProjectResponse } from "../models/ProjectResponse"
import { UserResponse } from "../models/UserResponse"
import { AdminResponse } from "../models/AdminResponse"

export type AppActionType =
  | "PROJECT_LIST_LOADING"
  | "PROJECT_LIST_SUCCESS"
  | "PROJECT_DETAIL_LOADING"
  | "PROJECT_DETAIL_SUCCESS"
  | "PROJECT_DETAIL_FAILURE"
  | "PROJECT_DETAIL_CHANGED"
  | "USER_DETAIL_LOADING"
  | "USER_DETAIL_SUCCESS"
  | "ADMIN_DETAIL_LOADING"
  | "ADMIN_DETAIL_SUCCESS"
  | "ADMIN_DETAIL_FAILURE"

export interface AppAction<T extends AppActionType, P> {
  type: T
  payload: P
}

export type ProjectDetailLoadingAction = AppAction<"PROJECT_DETAIL_LOADING", undefined>

export type ProjectDetailSuccessAction = AppAction<"PROJECT_DETAIL_SUCCESS", ProjectResponse>

export type ProjectDetailFailureAction = AppAction<"PROJECT_DETAIL_FAILURE", undefined>

export type ProjectDetailChangedAction = AppAction<"PROJECT_DETAIL_CHANGED", undefined>

export type UserDetailLoadingAction = AppAction<"USER_DETAIL_LOADING", undefined>

export type UserDetailSuccessAction = AppAction<"USER_DETAIL_SUCCESS", UserResponse>

export type AdminDetailLoadingAction = AppAction<"ADMIN_DETAIL_LOADING", undefined>

export type AdminDetailSuccessAction = AppAction<"ADMIN_DETAIL_SUCCESS", AdminResponse>

export type AdminDetailFailureAction = AppAction<"ADMIN_DETAIL_FAILURE", undefined>

export const projectDetailLoading = (): ProjectDetailLoadingAction => ({
  type: "PROJECT_DETAIL_LOADING",
  payload: undefined
})

export const projectDetailChanged = (): ProjectDetailChangedAction => ({
  type: "PROJECT_DETAIL_CHANGED",
  payload: undefined
})

export const projectDetailSuccess = (project: ProjectResponse): ProjectDetailSuccessAction => ({
  type: "PROJECT_DETAIL_SUCCESS",
  payload: project
})

export const projectDetailFailure = (): ProjectDetailFailureAction => ({
  type: "PROJECT_DETAIL_FAILURE",
  payload: undefined
})

export const userDetailLoading = (): UserDetailLoadingAction => ({
  type: "USER_DETAIL_LOADING",
  payload: undefined
})

export const userDetailSuccess = (user: UserResponse): UserDetailSuccessAction => ({
  type: "USER_DETAIL_SUCCESS",
  payload: user
})

export const adminDetailLoading = (): AdminDetailLoadingAction => ({
  type: "ADMIN_DETAIL_LOADING",
  payload: undefined
})

export const adminDetailSuccess = (user: AdminResponse): AdminDetailSuccessAction => ({
  type: "ADMIN_DETAIL_SUCCESS",
  payload: user
})

export const adminDetailFailure = (): AdminDetailFailureAction => ({
  type: "ADMIN_DETAIL_FAILURE",
  payload: undefined
})
