import { UseQueryResult } from '@tanstack/react-query';
import useImages from '@/modules/image/hooks/useImages';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from '@/components/ui/carousel';
import { Card, CardContent } from '@/components/ui/card';
import { FileImage, ImageOff } from 'lucide-react';

type ImageResponse = {
  url: string;
  mimeType: string;
  metadata: {
    alt: string;
  };
};

interface ProductImageCarouselProps {
  imagesUuid: string[];
}

const ProductImageCarousel = ({ imagesUuid }: ProductImageCarouselProps) => {
  const imageQueries: UseQueryResult<ImageResponse, Error>[] = useImages(imagesUuid);

  return imageQueries.length === 0 ? (
    <NoImageCarousel />
  ) : (
    <ImageCarousel imageQueries={imageQueries} imagesUuid={imagesUuid} />
  );
};

const NoImageCarousel = () => (
  <div className="w-full flex items-center justify-center overflow-x-auto">
    <Carousel className="w-full max-w-sm">
      <CarouselContent>
        <CarouselItem>
          <div className="p-1">
            <Card>
              <CardContent className="flex aspect-square items-center justify-center p-6">
                <div className="w-full flex flex-col items-center justify-center">
                  <ImageOff className="w-20 h-20" />
                  <p className="mt-2 text-lg">This product has no images yet.</p>
                </div>
              </CardContent>
            </Card>
          </div>
        </CarouselItem>
      </CarouselContent>
      <CarouselPrevious />
      <CarouselNext />
    </Carousel>
  </div>
);

interface ImageCarouselProps {
  imageQueries: UseQueryResult<ImageResponse, Error>[];
  imagesUuid: string[];
}

const ImageCarousel = ({ imageQueries, imagesUuid }: ImageCarouselProps) => (
  <div className="w-full flex items-center justify-center overflow-x-auto">
    <Carousel className="w-full max-w-3xl">
      <CarouselContent>
        {imageQueries.map((query, index) => {
          const { data, isLoading, error } = query;
          return (
            <CarouselItem key={imagesUuid[index]} className="basis-1/3">
              <div className="p-1">
                <Card>
                  <CardContent className="flex aspect-square items-center justify-center p-6">
                    {isLoading && <FileImage className="w-16 h-16 text-gray-400" />}
                    {error && <ImageOff className="w-16 h-16 text-red-400" />}
                    {data && (
                      <img src={data.url} alt={data.metadata.alt} width={500} height={500} />
                    )}
                  </CardContent>
                </Card>
              </div>
            </CarouselItem>
          );
        })}
      </CarouselContent>
      <CarouselPrevious />
      <CarouselNext />
    </Carousel>
  </div>
);

export default ProductImageCarousel;
