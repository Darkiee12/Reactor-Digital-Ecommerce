import React from 'react';
import { useFieldArray, useFormContext } from 'react-hook-form';

interface SpecsProps {
  baseName: string;
}

export const SpecificationsTable: React.FC<SpecsProps> = ({ baseName }) => {
  const { control, register } = useFormContext();
  const { fields, append, remove } = useFieldArray<{ [key: string]: any }>({
    control,
    name: baseName as any,
  });

  return (
    <div className="p-2">
      <table className="w-full border-collapse border">
        <thead>
          <tr>
            <th className="border p-1">Key</th>
            <th className="border p-1">Value</th>
            <th className="border p-1" />
          </tr>
        </thead>
        <tbody>
          {fields.map((field, idx) => (
            <tr key={field.id}>
              <td className="border p-1">
                <input
                  {...register(`${baseName}.${idx}.key` as const)}
                  className="border px-2 py-1 w-full"
                />
              </td>
              <td className="border p-1">
                <input
                  {...register(`${baseName}.${idx}.value` as const)}
                  className="border px-2 py-1 w-full"
                />
              </td>
              <td className="border p-1 text-center">
                <button type="button" onClick={() => remove(idx)} className="text-red-500">
                  Remove
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <button
        type="button"
        onClick={() => append({ key: '', value: '' })}
        className="mt-2 px-4 py-1 bg-blue-500 text-white rounded"
      >
        Add Specification
      </button>
    </div>
  );
};
