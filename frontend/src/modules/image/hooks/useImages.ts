import { useQueries } from '@tanstack/react-query';
import ImageService from '../service/imageApi';

const useImages = (imageUuids: string[]) =>
  useQueries({
    queries: imageUuids.map(uuid => ({
      queryKey: ['image', uuid],
      queryFn: () => ImageService.getImage(uuid),
    })),
  });

export default useImages;
