import ErrorResponse from '@/modules/error/model/Error';
import api from '@/shared/response/api/axios';
import { Metadata } from '@/modules/image/model/metadata';
import DataResponse from '@/shared/response/model/DataResponse';
import { AxiosError, AxiosResponse } from 'axios';
import { Result } from 'ts-results';
const getImageData = async (imageUuid: string) => {
  const response = await api.get(`/api/v1/images/${imageUuid}`, {
    responseType: 'blob',
  });
  const blob = new Blob([response.data], { type: response.headers['content-type'] });
  const url = URL.createObjectURL(blob);
  return url;
};

const getImageMetadata = async (imageUuid: string) => {
  return Result.wrapAsync<AxiosResponse<DataResponse<Metadata>, any>, AxiosError<ErrorResponse>>(
    () => api.get<DataResponse<Metadata>>(`/image/${imageUuid}/metadata`)
  );
};

const getImage = async (imageUuid: string) => {
  const [imageRes, metadataRes] = await Promise.all([
    api.get(`/images/uuid/${imageUuid}`, { responseType: 'blob' }),
    api.get<DataResponse<Metadata>>(`/images/uuid/${imageUuid}/metadata`),
  ]);

  const mimeType = imageRes.headers['content-type'];
  const blob = new Blob([imageRes.data], { type: mimeType });
  const url = URL.createObjectURL(blob);

  return {
    url,
    mimeType,
    metadata: metadataRes.data.item,
  };
};

const ImageService = {
  getImage,
};
export default ImageService;
