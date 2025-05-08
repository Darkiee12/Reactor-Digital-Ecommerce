import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import { useCategories } from '@/modules/category/hooks/useCategories';
import { Loader2 } from 'lucide-react';
import useProducts from '../hooks/useProducts';
import { useState } from 'preact/hooks';
import DisplayMultipleProducts from '@/modules/product/components/MultipleProduct';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationEllipsis,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import { Category } from '@/modules/category/model/Category';

const FindAllProductsByCategorySegment = () => {
  const [selectedCategory, setSelectedCategory] = useState<number>();
  const [page, setPage] = useState(0);
  const [size] = useState(100);
  const [searchTerm, setSearchTerm] = useState('');

  const {
    data: categories,
    isLoading: categoriesLoading,
    error: categoryQueryError,
  } = useCategories({});

  const {
    data: products,
    isLoading: productsLoading,
    error: productQueryError,
  } = useProducts({ type: 'category', value: selectedCategory }, page, size, {
    queryKey: ['category', selectedCategory],
    enabled: !!selectedCategory,
  });

  // Handle category selection
  const handleSelectCategory = (category: Category) => {
    setSelectedCategory(category.id);
    setSearchTerm(category.name);
    setPage(0);
  };

  // Generate page numbers for pagination display
  const getPageNumbers = (currentPage: number, totalPages: number) => {
    if (totalPages <= 5) {
      return Array.from({ length: totalPages }, (_, i) => i + 1);
    }

    const pages: (number | string)[] = [];
    pages.push(1); // Always show first page

    if (currentPage >= 4) {
      pages.push('...');
    }

    const start = Math.max(2, currentPage - 1);
    const end = Math.min(totalPages - 1, currentPage + 1);
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    if (currentPage <= totalPages - 3) {
      pages.push('...');
    }

    if (totalPages > 1) {
      pages.push(totalPages); // Always show last page
    }

    return pages;
  };

  // Pagination navigation handlers
  const handleNextPage = () => {
    if (products?.page.hasNext) {
      setPage(prev => prev + 1);
    }
  };

  const handlePreviousPage = () => {
    if (products?.page.hasPrevious) {
      setPage(prev => prev - 1);
    }
  };

  return (
    <div className="pl-8 pr-4 sm:pr-10 md:pr-32 flex flex-col space-y-4 py-5">
      <Command className="rounded-lg border shadow-md md:min-w-[450px]">
        <CommandInput
          placeholder="Search for a category..."
          value={searchTerm}
          onValueChange={setSearchTerm}
        />
        <CommandList>
          {categoriesLoading && (
            <div className="flex justify-center p-4">
              <Loader2 className="h-6 w-6 animate-spin" />
            </div>
          )}
          {categoryQueryError && <CommandEmpty>Error: {categoryQueryError.message}</CommandEmpty>}
          {!categoriesLoading && !categoryQueryError && (
            <>
              <CommandEmpty>No categories found.</CommandEmpty>
              <CommandGroup heading="Categories">
                {categories?.items.map(category => (
                  <CommandItem
                    key={category.id}
                    value={category.name}
                    onSelect={() => handleSelectCategory(category)}
                  >
                    <span>{category.name}</span>
                  </CommandItem>
                ))}
              </CommandGroup>
            </>
          )}
        </CommandList>
      </Command>

      {productsLoading ? (
        <div className="mt-4">Loading products...</div>
      ) : productQueryError ? (
        <div className="mt-4">Error: {productQueryError.message}</div>
      ) : (
        <>
          <DisplayMultipleProducts products={products?.items || []} />
          {products && products.page.totalPages > 1 && (
            <Pagination className="mt-4">
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    onClick={handlePreviousPage}
                    className={products.page.hasPrevious ? 'block' : 'hidden'}
                  />
                </PaginationItem>
                {getPageNumbers(page + 1, products.page.totalPages).map((pageNum, index) =>
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
                  <PaginationNext
                    onClick={handleNextPage}
                    className={products.page.hasNext ? 'block' : 'hidden'}
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          )}
        </>
      )}
    </div>
  );
};

export default FindAllProductsByCategorySegment;
