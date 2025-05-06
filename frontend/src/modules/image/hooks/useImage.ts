import { useQuery } from '@tanstack/react-query';
import ImageService from '../service/imageApi';

const useImage = (imageUuid: string) => {
  return useQuery({
    queryKey: ['image', imageUuid],
    queryFn: () => ImageService.getImage(imageUuid),
    enabled: !!imageUuid,
  });
};

export default useImage;
