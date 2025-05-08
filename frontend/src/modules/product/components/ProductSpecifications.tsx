import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';

const ProductSpecifications = ({ data }: { data: { [key: string]: any } }) => (
  <div className="border-[#3D444D] border-2 my-5 rounded-lg min-w-4xl overflow-x-auto">
    <Table className="hidden md:table">
      <TableHeader>
        <TableRow className="dark:bg-gray-900 bg-gray-100">
          <TableHead colSpan={2}>Specifications</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {Object.entries(data).map(([key, value]) => (
          <TableRow key={key}>
            <TableCell className="text-left">{key}</TableCell>
            <TableCell className={typeof value === 'number' ? 'text-right' : 'text-left'}>
              {value}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
    <div className="md:hidden p-4">
      {Object.entries(data).map(([key, value]) => (
        <div key={key} className="flex justify-between py-2 border-b border-gray-200">
          <span className="font-medium">{key}</span>
          <span className={typeof value === 'number' ? 'text-right' : ''}>{value}</span>
        </div>
      ))}
    </div>
  </div>
);

export default ProductSpecifications;
