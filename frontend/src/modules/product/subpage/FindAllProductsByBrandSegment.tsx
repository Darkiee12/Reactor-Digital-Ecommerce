import { Button } from '@/components/ui/button';
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
import useBrands from '@/modules/brand/hooks/useBrand';

const FindAllByBrand = () => {
  const [currentSearchTerm, setCurrentSearchTerm] = useState('');
  const [selectedBrand, setSelectedBrand] = useState<number>();
  const [page, setPage] = useState(0);

  const { data: brandData, isLoading: isBrandsLoading } = useBrands(currentSearchTerm, 0, {
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

  // Pagination handlers
  const handleNextPage = () => {
    if (productData?.page.hasNext) {
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
      <Command className="rounded-lg border shadow-md md:min-w-[450px]">
        <CommandInput
          placeholder="Search a brand here"
          value={currentSearchTerm}
          onValueChange={setCurrentSearchTerm}
          className=" bg-none"
        />
        <CommandList>
          {isBrandsLoading ? (
            <CommandEmpty>{currentSearchTerm.length > 3 && 'Loading brands...'}</CommandEmpty>
          ) : brandData?.items.length ? (
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
          {productData && (
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
                Page {page + 1}/{productData.page.totalPages}
              </span>
              <Button
                className="dark:bg-green-600 bg-green-400"
                onClick={handleNextPage}
                disabled={!productData?.page.hasNext}
                variant="outline"
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

export default FindAllByBrand;
