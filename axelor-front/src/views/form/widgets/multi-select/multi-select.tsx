import { useAtom } from "jotai";
import { useCallback } from "react";

import { SelectOptionProps } from "@/components/select";
import { Selection as SelectionType } from "@/services/client/meta.types";

import { FieldProps } from "../../builder";
import { Selection, SelectionTag } from "../selection";

export function MultiSelect(props: FieldProps<string | number | null>) {
  const { readonly, valueAtom } = props;
  const [value, setValue] = useAtom(valueAtom);

  const removeItem = useCallback(
    (item: SelectionType) => {
      const items = value ? String(value).split(",") : [];
      const next = items
        .filter((x) => String(x) !== String(item.value))
        .join(",");
      setValue(next, true);
    },
    [setValue, value],
  );

  const renderValue = useCallback(
    ({ option }: SelectOptionProps<SelectionType>) => {
      return (
        <SelectionTag
          title={option.title}
          color={option.color}
          onRemove={readonly ? undefined : () => removeItem(option)}
        />
      );
    },
    [readonly, removeItem],
  );

  const renderOption = useCallback(
    ({ option }: SelectOptionProps<SelectionType>) => {
      return <SelectionTag title={option.title} color={option.color} />;
    },
    [],
  );

  return (
    <Selection
      {...props}
      multiple={true}
      renderValue={renderValue}
      renderOption={renderOption}
    />
  );
}
