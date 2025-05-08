import useProducts from '@/modules/product/hooks/useProducts';
import { ProductsFinder } from '@/modules/product/model/enums';
import DisplayMultipleProducts from '@/modules/product/components/MultipleProduct';
import { useState } from 'react';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from '@/components/ui/command';
import Brand from '@/modules/brand/model/brand';
import useBrandSearch from '@/modules/brand/hooks/useBrandSearch';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationEllipsis,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';

const FindAllByBrand = () => {
  const [currentSearchTerm, setCurrentSearchTerm] = useState('');
  const [selectedBrand, setSelectedBrand] = useState<number>();
  const [page, setPage] = useState(0);

  const { data: brandData, isLoading: isBrandsLoading } = useBrandSearch(currentSearchTerm, 0, {
    queryKey: ['brands', 'search', currentSearchTerm, page],
    enabled: !!currentSearchTerm,
  });

  const finder: ProductsFinder = { type: 'brand', value: selectedBrand };
  const { data: productData, isLoading: isProductsLoading, error } = useProducts(finder, page);

  const handleSelectBrand = (brand: Brand) => {
    setSelectedBrand(brand.id);
    setCurrentSearchTerm(brand.name);
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
    if (productData?.page.hasNext) {
      setPage(prev => prev + 1);
    }
  };

  const handlePreviousPage = () => {
    if (productData?.page.hasPrevious) {
      setPage(prev => prev - 1);
    }
  };

  return (
    <div className="pl-8 pr-4 sm:pr-10 md:pr-32 flex flex-col space-y-4 py-5">
      <Command className="rounded-lg border shadow-md md:min-w-[450px]">
        <CommandInput
          placeholder="Search a brand here"
          value={currentSearchTerm}
          onValueChange={setCurrentSearchTerm}
          className="bg-none"
        />
        <CommandList>
          {isBrandsLoading ? (
            <CommandEmpty>{currentSearchTerm.length > 3 && 'Loading brands...'}</CommandEmpty>
          ) : brandData?.items ? (
            <CommandGroup heading="Brands">
              {currentSearchTerm.length > 2 &&
                brandData.items.map((brand: Brand) => (
                  <CommandItem
                    key={brand.id}
                    value={brand.name}
                    onSelect={() => handleSelectBrand(brand)}
                  >
                    {brand.name}
                  </CommandItem>
                ))}
            </CommandGroup>
          ) : (
            <CommandEmpty>No results found.</CommandEmpty>
          )}
          <CommandSeparator />
        </CommandList>
      </Command>

      {isProductsLoading ? (
        <div>Loading products...</div>
      ) : error ? (
        <div>Error: {error.message}</div>
      ) : (
        <>
          <DisplayMultipleProducts products={productData?.items || []} />
          {productData && productData.page.totalPages > 1 && (
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    onClick={handlePreviousPage}
                    className={productData.page.hasPrevious ? 'block' : 'hidden'}
                  />
                </PaginationItem>
                {getPageNumbers(page + 1, productData.page.totalPages).map((pageNum, index) =>
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
                    className={productData.page.hasNext ? 'block' : 'hidden'}
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

export default FindAllByBrand;
