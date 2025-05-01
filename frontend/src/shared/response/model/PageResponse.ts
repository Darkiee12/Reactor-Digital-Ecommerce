import BaseResponse from "./BaseResponse";

export default interface PageResponse<T> extends BaseResponse{
    items: T[];
    page: Metadata;

}

export interface Metadata {
    page:        number;
    size:        number;
    totalPages:  number;
    totalItems:  number;
    hasNext:     boolean;
    hasPrevious: boolean;
}
