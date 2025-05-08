import { useState } from 'react';
import { z } from 'zod';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import useProductByUuid from '@/modules/product/hooks/useProduct';
import useImages from '@/modules/image/hooks/useImages';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Spinner } from '@/components/ui/spinner';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from '@/components/ui/carousel';
import { Card, CardContent } from '@/components/ui/card';
import { Search } from 'lucide-react';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { Link } from 'react-router-dom';

const uuidSchema = z.object({
  uuid: z.string().uuid({ message: 'Invalid UUID format' }),
});

type UuidForm = z.infer<typeof uuidSchema>;

const FindByUuidSegment: React.FC = () => {
  const [searchUuid, setSearchUuid] = useState('');

  const form = useForm<UuidForm>({
    resolver: zodResolver(uuidSchema),
    defaultValues: { uuid: '' },
  });

  const {
    data: productData,
    isLoading: productLoading,
    error: productError,
  } = useProductByUuid(searchUuid);

  const imageUuids = productData?.imagesUuid ?? [];
  const imageQueries = useImages(imageUuids);

  const imagesLoading = imageQueries.some(q => q.isLoading);
  const imagesError = imageQueries.find(q => q.error)?.error;

  const onSubmit = (values: UuidForm) => {
    setSearchUuid(values.uuid.trim());
  };

  return (
    <div className="w-full px-8 mx-auto">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="flex space-x-2 mb-6">
          <FormField
            control={form.control}
            name="uuid"
            render={({ field }) => (
              <FormItem className="flex-1">
                <FormControl>
                  <Input
                    {...field}
                    placeholder="Find a product with their uuid"
                    className="focus-visible:border-2 focus-visible:border-blue-600"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit" className="dark:bg-green-600 bg-green-400 px-4 py-4">
            <Search className="mr-2" /> Search
          </Button>
        </form>
      </Form>

      {(productLoading || imagesLoading) && <Spinner className="mx-auto" />}
      {(productError || imagesError) && (
        <p className="text-red-600 text-center">
          Error: {(productError || (imagesError as Error))?.message}
        </p>
      )}

      {productData && !productLoading && !imagesLoading && (
        <div className="p-4 shadow rounded-lg border border-gray-200">
          <Carousel className="w-full max-w-md mx-auto">
            <CarouselContent>
              {imageQueries.map(q => (
                <CarouselItem key={q.data?.metadata.uuid} className="p-1 basis-1/3">
                  <Card>
                    <CardContent className="flex aspect-square items-center justify-center p-4">
                      <img
                        src={q.data?.url}
                        alt={q.data?.metadata.alt || 'Product image'}
                        className="object-cover rounded"
                      />
                    </CardContent>
                  </Card>
                </CarouselItem>
              ))}
            </CarouselContent>
            <CarouselPrevious />
            <CarouselNext />
          </Carousel>

          <div className="mt-6 text-center">
            <p className="text-lg font-semibold">{productData.name}</p>
            <p className="font-mono text-sm mb-4">{productData.uuid}</p>
            <Button
              variant="secondary"
              className="mt-6 dark:bg-green-600 bg-green-400 hover:bg-green-500 hover:dark:brightness-200"
            >
              <Link to={`/products/${productData.uuid}`}>View Detail</Link>
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default FindByUuidSegment;
