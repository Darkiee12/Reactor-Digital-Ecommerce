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
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationEllipsis,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';

// Define the form schema using Zod
const formSchema = z.object({
  searchTerm: z.string().min(3, {
    message: 'Searching keywords must be at least 3 characters long',
  }),
});

type FormValues = z.infer<typeof formSchema>;

const FindAllByName = () => {
  const [currentSearchTerm, setCurrentSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(20); // Default page size
  const finder: ProductsFinder = { type: 'name', value: currentSearchTerm };
  const { data, isLoading, error } = useProducts(finder, page, size, {
    queryKey: ['products', 'search', currentSearchTerm, page],
    enabled: !!currentSearchTerm,
  });

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: { searchTerm: '' },
  });

  // Handle form submission
  const onSubmit = (values: FormValues) => {
    setCurrentSearchTerm(values.searchTerm);
    setPage(0); // Reset to first page on new search
  };

  // Generate page numbers for pagination display
  const getPageNumbers = (currentPage: number, totalPages: number) => {
    if (totalPages <= 5) {
      return Array.from({ length: totalPages }, (_, i) => i + 1);
    }

    const pages: (number | string)[] = [];

    // Always show first page
    pages.push(1);

    // Show ellipsis if currentPage is 4 or higher
    if (currentPage >= 4) {
      pages.push('...');
    }

    // Show pages around currentPage
    const start = Math.max(2, currentPage - 1);
    const end = Math.min(totalPages - 1, currentPage + 1);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    // Show ellipsis if currentPage is 3 or more pages from the end
    if (currentPage <= totalPages - 3) {
      pages.push('...');
    }

    // Always show last page if more than one page exists
    if (totalPages > 1) {
      pages.push(totalPages);
    }

    return pages;
  };

  // Pagination navigation handlers
  const handleNextPage = () => {
    if (data?.page.hasNext) {
      setPage(prev => prev + 1);
    }
  };

  const handlePreviousPage = () => {
    if (data?.page.hasPrevious) {
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
          {data && data.page.totalPages > 1 && (
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious onClick={handlePreviousPage} />
                </PaginationItem>
                {getPageNumbers(page + 1, data.page.totalPages).map((pageNum, index) =>
                  pageNum === '...' ? (
                    <PaginationItem key={index}>
                      <PaginationEllipsis />
                    </PaginationItem>
                  ) : (
                    <PaginationItem key={index}>
                      <PaginationLink
                        href="#"
                        isActive={pageNum === page + 1}
                        onClick={() => setPage((pageNum as number) - 1)}
                      >
                        {pageNum}
                      </PaginationLink>
                    </PaginationItem>
                  )
                )}
                <PaginationItem>
                  <PaginationNext onClick={handleNextPage} />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          )}
        </>
      )}
    </div>
  );
};

export default FindAllByName;
