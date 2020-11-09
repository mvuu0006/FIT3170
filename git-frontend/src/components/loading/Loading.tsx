import React, { FunctionComponent } from "react"
import styles from "./Loading.module.css"

interface LoadingProps {
  iconColor: "white" | "black"
}

const Loading: FunctionComponent<LoadingProps> = ({ iconColor }) => {
  const iconStyle = [styles.Loading, iconColor === "white" ? styles.LoadingWhite : styles.LoadingBlack].join(" ")

  return (
    <div className={iconStyle}>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
    </div>
  )
}
export default Loading
