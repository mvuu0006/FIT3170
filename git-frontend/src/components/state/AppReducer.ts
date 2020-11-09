import { AppAction, AppActionType, ProjectDetailSuccessAction, UserDetailSuccessAction, AdminDetailSuccessAction } from "./AppAction"
import { AppState } from "./AppState"
import { Reducer } from "react"
import { AppStatus } from "../models/AppStatus"

const AppReducer: Reducer<AppState, AppAction<AppActionType, any>> = (prevState, action): AppState => {
  switch (action.type) {
    case "PROJECT_DETAIL_LOADING": {
      return {
        ...prevState,
        projectDetailStatus: AppStatus.LOADING
      }
    }

    case "PROJECT_DETAIL_SUCCESS": {
      const projectSuccessAction = action as ProjectDetailSuccessAction

      return {
        ...prevState,
        currentProject: {
          users: projectSuccessAction.payload.users,
          projects: projectSuccessAction.payload.projects,
        },
        projectDetailStatus: AppStatus.SUCCESS
      }
    }

    case "PROJECT_DETAIL_FAILURE": {
      return {
        ...prevState,
        projectDetailStatus: AppStatus.FAILURE
      }
    }

     case "PROJECT_DETAIL_CHANGED": {
          return {
            ...prevState,
            projectDetailStatus: AppStatus.CHANGED
          }
        }

    case "USER_DETAIL_LOADING": {
      return {
        ...prevState,
        userDetailStatus: AppStatus.LOADING
      }
    }

    case "USER_DETAIL_SUCCESS": {
      const userSuccessAction = action as UserDetailSuccessAction
      const projects = []
      for (let project of userSuccessAction.payload.projects) {
        
      }

      return {
        ...prevState,
        user: {
          givenName: userSuccessAction.payload.firstName,
          familyName: userSuccessAction.payload.lastName,
          emailAddress: userSuccessAction.payload.emailAddress,
          userGroup: userSuccessAction.payload.userGroup,
          projects: projects
        },
        userDetailStatus: AppStatus.SUCCESS
      }
    }
    case "ADMIN_DETAIL_LOADING": {
          return {
            ...prevState,
            adminDetailStatus: AppStatus.LOADING
          }
        }

        case "ADMIN_DETAIL_SUCCESS": {
          const adminSuccessAction = action as AdminDetailSuccessAction
                return {
                  ...prevState,
                  admin: {
                    isAdmin: adminSuccessAction.payload.isAdmin
                  },
                  adminDetailStatus: AppStatus.SUCCESS
                }
          }

            case "ADMIN_DETAIL_FAILURE": {
              return {
                ...prevState,
                adminDetailStatus: AppStatus.FAILURE
              }
            }

    default: {
      throw Error(`Action ${action.type} is not recognised`)
    }
  }
}

export default AppReducer
