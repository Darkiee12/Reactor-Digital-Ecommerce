import { useState } from 'react';
import useProductByUuid from '@/modules/product/hooks/useProduct';
import { Product } from '@/modules/product/model/Product';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import {
  Form,
  FormField,
  FormControl,
  FormMessage,
  FormItem,
  FormLabel,
} from '@/components/ui/form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Spinner } from '@/components/ui/spinner';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from '@/components/ui/carousel';
import { Card, CardContent } from '@/components/ui/card';
import useImages from '@/modules/image/hooks/useImages';
import { Input } from '@/components/ui/input';
import { Search } from 'lucide-react';

const formSchema = z.object({
  uuid: z.string().uuid('Invalid UUID format'),
});

type FormValues = z.infer<typeof formSchema>;

const FindByUuidSegment: React.FC = () => {
  const [searchUuid, setSearchUuid] = useState<string>('');
  const {
    data: productData,
    isLoading: productLoading,
    error: productError,
  } = useProductByUuid(searchUuid);
  const imageUuids = productData?.data.item.imagesUuid ?? [];
  const imageQueries = useImages(imageUuids);

  const imagesLoading = imageQueries.some(q => q.isLoading);
  const imagesError = imageQueries.find(q => q.error)?.error;

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: { uuid: '' },
  });

  const onSubmit = (values: FormValues) => {
    setSearchUuid(values.uuid);
  };

  return (
    <div className="w-full px-8">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="flex space-x-2">
          <FormField
            control={form.control}
            name="uuid"
            render={({ field }) => (
              <FormItem className="flex-1">
                <FormControl>
                  <Input
                    {...field}
                    placeholder="Find a product with their uuid"
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23]"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit" variant="ghost" className="bg-[#238636] px-4 py-4">
            <Search /> Search
          </Button>
        </form>
      </Form>

      <div className="mt-6">
        {(productLoading || imagesLoading) && <Spinner className="mx-auto" />}
        {(productError || imagesError) && (
          <p className="text-red-600">Error: {(productError || (imagesError as Error))?.message}</p>
        )}

        {productData && !productLoading && !imagesLoading && (
          <div className="p-2 transition-all duration-200 ease-in-out">
            <div className="flex flex-col justify-center items-center gap-x-2 ">
              <Carousel className="w-full max-w-md mt-4 bg-none rounded-lg">
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
              <ProductRenderer product={productData.data.item} />
              <SpecificationRenderer specifications={productData.data.item.specifications} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

const productSchema = z.object({
  name: z
    .string()
    .min(2, 'Product name must be at least 2 characters')
    .max(100, 'Product name must be less than 100 characters'),
  brandName: z
    .string()
    .min(2, 'Brand name must be at least 2 characters')
    .max(100, 'Brand name must be less than 100 characters'),
  description: z.string().max(500, 'Description must be less than 500 characters').optional(),
  price: z.number().min(0, 'Price must be non-negative').max(1000000, 'Price is too high'),
  stockQuantity: z
    .number()
    .int()
    .min(0, 'Stock quantity must be non-negative')
    .max(100000, 'Stock quantity is too high'),
  categories: z.array(z.string()).min(1, 'At least one category is required'),
  specifications: z.record(z.string()).optional(),
});

type ProductFormValues = z.infer<typeof productSchema>;

const ProductRenderer = ({ product }: { product: Product }) => {
  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productSchema),
    defaultValues: {
      name: product.name,
      brandName: product.brandName,
      description: product.description,
      price: product.price,
      stockQuantity: product.stockQuantity,
      categories: product.categories,
    },
  });

  const onSubmit = async (values: ProductFormValues) => {
    console.log('Updated product data:', values);
  };

  return (
    <div className="w-full p-6 rounded-lg shadow-md">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col gap-y-5">
          <FormField
            control={form.control}
            name="name"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Product Name</FormLabel>
                <FormControl>
                  <Input
                    placeholder="Enter product name"
                    {...field}
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23] text-white"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="brandName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Brand</FormLabel>
                <FormControl>
                  <Input
                    placeholder="Enter brand name"
                    {...field}
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23] text-white"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="description"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Description</FormLabel>
                <FormControl>
                  <Textarea
                    placeholder="Enter product description"
                    {...field}
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23] text-white"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="price"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Price</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    step="0.01"
                    placeholder="Enter price"
                    {...field}
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23] text-white"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="stockQuantity"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Stock Quantity</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    placeholder="Enter stock quantity"
                    {...field}
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23] text-white"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="categories"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Categories (comma-separated)</FormLabel>
                <FormControl>
                  <Input
                    placeholder="Enter categories (e.g., electronics, gadgets)"
                    value={field.value.join(', ')}
                    className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23] text-white"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <Button
            type="submit"
            variant="ghost"
            disabled={form.formState.isSubmitting}
            className="mt-4 bg-[#238636] text-white"
          >
            {form.formState.isSubmitting ? 'Updating...' : 'Update Product'}
          </Button>

          {form.formState.errors.root && (
            <div className="text-red-500 text-sm mt-2">{form.formState.errors.root.message}</div>
          )}
        </form>
      </Form>
    </div>
  );
};

const SpecificationRenderer = ({ specifications }: { specifications: { [key: string]: any } }) => {
  return (
    <table className="w-full text-sm border-collapse">
      <tbody>
        <tr>
          <td colSpan={2} className="p-0">
            <table className="w-full table-fixed border-collapse border border-gray-300">
              <tbody>
                {Object.entries(specifications).map(([key, val]) => (
                  <tr key={key} className="border-t border-gray-300">
                    <td className="w-1/2 border-r border-gray-300 px-3 py-2 font-medium text-sm align-top">
                      {key}
                    </td>
                    <td className="w-1/2 px-3 py-2 text-sm">{String(val)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </td>
        </tr>
      </tbody>
    </table>
  );
};

const Row = ({ label, value }: { label: string; value: string | number }) => (
  <tr>
    <td className="font-medium py-1 w-32">{label}</td>
    <td className="py-1">{value}</td>
  </tr>
);

export default FindByUuidSegment;
