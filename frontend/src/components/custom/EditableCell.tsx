import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface EditableCellProps {
  name: string;
  type?: string;
  readOnly?: boolean;
}

export const EditableCell: React.FC<EditableCellProps> = ({
  name,
  type = 'text',
  readOnly = false,
}) => {
  const { control } = useFormContext();
  return (
    <Controller
      name={name as any}
      control={control}
      render={({ field }) => (
        <input
          {...field}
          type={type}
          readOnly={readOnly}
          className={`border px-2 py-1 w-full ${readOnly ? 'bg-gray-100' : 'bg-white'}`}
        />
      )}
    />
  );
};
