import { useState, useMemo } from 'preact/hooks';
import { ArrowUp, ArrowDown } from 'lucide-react';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import { ProductIcon } from '@/modules/product/components/ProductIcon';
import { Category } from '../model/Category';

export interface Brand {
  id: number;
  name: string;
  count: number;
}

type SortKey = keyof Category;
type SortDirection = 'asc' | 'desc';

const BrandTable = ({ categories }: { categories: Category[] }) => {
  const [sortKey, setSortKey] = useState<SortKey>('id');
  const [sortDirection, setSortDirection] = useState<SortDirection>('asc');

  const onSortChange = (key: SortKey) => {
    if (sortKey === key) {
      setSortDirection(prev => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDirection('asc');
    }
  };

  const getSortDirection = (key: SortKey): SortDirection | null => {
    return sortKey === key ? sortDirection : null;
  };

  const sortedCategories = useMemo(() => {
    return [...categories].sort((a, b) => {
      let cmp = 0;
      if (sortKey === 'name') {
        cmp = a.name.localeCompare(b.name);
      } else if (sortKey === 'count') {
        cmp = a.count - b.count;
      } else {
        cmp = a.id - b.id;
      }
      return sortDirection === 'asc' ? cmp : -cmp;
    });
  }, [categories, sortKey, sortDirection]);

  return (
    <div className="border-[#3D444D] border-2 my-5 rounded-lg min-w-4xl overflow-x-auto">
      <Table>
        <TableHeader>
          <TableRow className="dark:bg-gray-900 bg-gray-100">
            <TableHead className="w-[50px] cursor-pointer" onClick={() => onSortChange('id')}>
              ID
              {getSortDirection('id') === 'asc' && <ArrowUp className="ml-2 h-4 w-4 inline" />}
              {getSortDirection('id') === 'desc' && <ArrowDown className="ml-2 h-4 w-4 inline" />}
            </TableHead>
            <TableHead className="w-[200px] cursor-pointer" onClick={() => onSortChange('name')}>
              Name
              {getSortDirection('name') === 'asc' && <ArrowUp className="ml-2 h-4 w-4 inline" />}
              {getSortDirection('name') === 'desc' && <ArrowDown className="ml-2 h-4 w-4 inline" />}
            </TableHead>
            <TableHead className="w-[100px]">Icon</TableHead>
            <TableHead
              className="text-right w-[80px] cursor-pointer"
              onClick={() => onSortChange('count')}
            >
              Count
              {getSortDirection('count') === 'asc' && <ArrowUp className="ml-2 h-4 w-4 inline" />}
              {getSortDirection('count') === 'desc' && (
                <ArrowDown className="ml-2 h-4 w-4 inline" />
              )}
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {sortedCategories.map(category => (
            <TableRow key={category.id} className="even:bg-gray-50 dark:even:bg-gray-900">
              <TableCell className="font-mono text-sm text-muted-foreground">
                {category.id}
              </TableCell>
              <TableCell className="font-medium">{category.name}</TableCell>
              <TableCell>{ProductIcon('category', category.name, 32)}</TableCell>
              <TableCell className="text-right">{category.count}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default BrandTable;
