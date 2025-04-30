import BaseResponse from "./BaseResponse";

export default interface MessageResponse extends BaseResponse{
    message: string;
}