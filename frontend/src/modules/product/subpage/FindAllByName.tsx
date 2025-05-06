import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Search } from 'lucide-react';
import useProducts from '@/modules/product/hooks/useProducts';
import { ProductsFinder } from '@/modules/product/model/enums';
import DisplayMultipleProducts from '@/modules/product/components/MultipleProduct';
import { useState } from 'react';

const formSchema = z.object({
  searchTerm: z.string().min(3, {
    message: 'Searching keywords must be at least 3 characters long',
  }),
});

type FormValues = z.infer<typeof formSchema>;

const FindAllByName = () => {
  const [currentSearchTerm, setCurrentSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const finder: ProductsFinder = { type: 'name', value: currentSearchTerm };
  const { data, isLoading, error } = useProducts(finder, page, {
    queryKey: ['products', 'search', currentSearchTerm, page],
    enabled: !!currentSearchTerm,
  });

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: { searchTerm: '' },
  });

  const onSubmit = (values: FormValues) => {
    setCurrentSearchTerm(values.searchTerm);
    setPage(0); // Reset page to 0 on new search
  };

  // Pagination handlers
  const handleNextPage = () => {
    if (data?.page.hasNext) {
      setPage(prev => prev + 1);
    }
  };

  const handlePreviousPage = () => {
    if (page > 0) {
      setPage(prev => prev - 1);
    }
  };

  return (
    <div className="pl-8 pr-4 sm:pr-10 md:pr-32 flex flex-col space-y-4 py-5">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="flex space-x-2">
          <FormField
            control={form.control}
            name="searchTerm"
            render={({ field }) => (
              <FormItem className="flex-1">
                <FormControl>
                  <Input
                    {...field}
                    placeholder="Find a product by name"
                    className="focus-visible:border-2 focus-visible:border-blue-600"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button
            type="submit"
            variant="outline"
            className="dark:bg-green-600 bg-green-400 px-4 py-4"
          >
            <Search /> Search
          </Button>
        </form>
      </Form>
      {isLoading ? (
        <div>Loading...</div>
      ) : error ? (
        <div>Error: {error.message}</div>
      ) : (
        <>
          <DisplayMultipleProducts products={data?.items || []} />
          {data && (
            <div className="flex justify-between mt-4">
              <Button
                onClick={handlePreviousPage}
                disabled={page === 0}
                variant="outline"
                className="dark:bg-green-600 bg-green-400"
              >
                Previous
              </Button>
              <span>
                Page {page + 1}/{data.page.totalItems}
              </span>
              <Button
                onClick={handleNextPage}
                disabled={!data?.page.hasNext}
                variant="outline"
                className="dark:bg-green-600 bg-green-400"
              >
                Next
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default FindAllByName;
