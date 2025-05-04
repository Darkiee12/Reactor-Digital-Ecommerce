import { Command, CommandInput } from '@/components/ui/command';
import { useState } from 'react';
import useProductByUuid from '@/modules/product/hooks/useProduct';
import { Product } from '@/modules/product/model/Product';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Form, FormField, FormControl, FormMessage, FormItem } from '@/components/ui/form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Spinner } from '@/components/ui/spinner';
const formSchema = z.object({
  uuid: z.string().uuid('Invalid UUID format'),
});

type FormValues = z.infer<typeof formSchema>;

const FindByUuidSegment: React.FC = () => {
  const [searchUuid, setSearchUuid] = useState<string>('');
  const { data: response, isLoading, error } = useProductByUuid(searchUuid);
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: { uuid: '' },
  });

  const onSubmit = (values: FormValues) => {
    console.log('Searching for UUID:', values.uuid);
    setSearchUuid(values.uuid);
  };

  return (
    <div className="w-full px-4">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="flex items-start space-x-2">
          <FormField
            control={form.control}
            name="uuid"
            render={({ field }) => (
              <FormItem className="flex-1">
                <FormControl>
                  <Command
                    {...field}
                    className="w-full rounded-lg border shadow-md md:min-w-[450px]"
                  >
                    <CommandInput placeholder="Look up a product by its uuid" />
                  </Command>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit" className="bg-[#238636] px-4 py-2 cursor-pointer" variant="ghost">
            Search
          </Button>
        </form>
      </Form>

      <div className="mt-6">
        {isLoading && <Spinner className="mx-auto" />}
        {error && (
          <p className="text-red-600">Error fetching product: {(error as Error).message}</p>
        )}
        {response?.ok && <ProductRenderer product={response.val.data.item} />}
      </div>
    </div>
  );
};

const ProductRenderer = ({ product }: { product: Product }) => {
  return (
    <div className="border rounded-lg shadow-md p-4">
      <h2 className="text-xl font-semibold mb-2">{product.name}</h2>
      <table className="w-full text-sm border-collapse">
        <tbody>
          <Row label="UUID" value={product.uuid} />
          <Row label="Brand" value={product.brandName} />
          <Row label="Description" value={product.description} />
          <Row label="Price" value={`₫${product.price.toFixed(2)}`} />
          <Row label="Stock" value={product.stockQuantity} />
          <Row label="Categories" value={product.categories.join(', ')} />
          <Row label="Created At" value={new Date(product.createdAt * 1000).toLocaleString()} />
          <Row label="Updated At" value={new Date(product.updatedAt * 1000).toLocaleString()} />
          {product.specifications && (
            <tr>
              <td className="font-medium py-1 align-top">Specifications</td>
              <td className="py-1">
                <ul className="list-disc list-inside space-y-1">
                  {Object.entries(product.specifications).map(([key, value]) => (
                    <li key={key}>
                      <strong>{key}:</strong> {String(value)}
                    </li>
                  ))}
                </ul>
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

const Row = ({ label, value }: { label: string; value: string | number }) => (
  <tr>
    <td className="font-medium py-1 w-32">{label}</td>
    <td className="py-1">{value}</td>
  </tr>
);

export default FindByUuidSegment;
