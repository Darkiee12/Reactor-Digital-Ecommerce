import { Metadata } from '@/modules/image/model/metadata';
import API from '@/shared/response/api/axios';

const api = API.getClient();
const getImage = async (imageUuid: string) => {
  const [image, metadata] = await Promise.all([
    api.getBlob(`/images/${imageUuid}`),
    api.getData<Metadata>(`/images/${imageUuid}/metadata`),
  ]);

  const url = URL.createObjectURL(image);

  return {
    url,
    metadata: metadata,
  };
};

const ImageService = {
  getImage,
};

export default ImageService;
