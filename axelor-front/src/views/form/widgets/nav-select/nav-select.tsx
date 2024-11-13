import { useAtomValue } from "jotai";
import uniqueId from "lodash/uniqueId";
import { useCallback, useMemo } from "react";

import {
  OverflowList,
  OverflowListItemProps,
  OverflowMenuTriggerProps,
  clsx,
} from "@axelor/ui";

import { DataRecord } from "@/services/client/data.types";
import { Selection } from "@/services/client/meta.types";

import { FieldControl, FieldProps, ValueAtom } from "../../builder";
import { createWidgetAtom } from "../../builder/atoms";
import { isReferenceField } from "../../builder/utils";
import { ManyToOne } from "../many-to-one";
import {
  SelectItem,
  useSelectionList,
  useSelectionValue,
} from "../selection/hooks";

import styles from "./nav-select.module.scss";

type ItemProps = OverflowListItemProps<SelectItem> & {
  readonly?: boolean;
};

function Item(props: ItemProps) {
  const { item, active, readonly } = props;
  const { selection } = item;
  return (
    <div
      className={clsx(styles.item, [
        {
          [styles.active]: active,
          [styles.readonly]: readonly,
        },
      ])}
    >
      <div className={styles.text}>{selection.title}</div>
    </div>
  );
}

function MenuTrigger({ count, open }: OverflowMenuTriggerProps) {
  return (
    <div className={clsx(styles.item, [{ [styles.open]: open }])}>
      <div className={styles.text}>+{count}</div>
    </div>
  );
}

function MenuItem({ item }: ItemProps) {
  const { selection } = item;
  return <>{selection.title}</>;
}

function isSelected(selection: Selection, value: any) {
  const selected = selection.value;
  const val = typeof value === "object" && value ? value.id : value;
  return selected === val || String(selected) === String(val);
}

export function NavSelect(
  props: FieldProps<string | number | Record<string, number>>,
) {
  const { schema, widgetAtom, formAtom, valueAtom } = props;
  const {
    attrs: { readonly },
  } = useAtomValue(widgetAtom);

  const [value, handleChange] = useSelectionValue(props, {
    disabled: readonly,
  });

  const isReference = useMemo(() => isReferenceField(schema), [schema]);

  const selection = useSelectionList({ value, widgetAtom, schema });
  const items: SelectItem[] = useMemo(() => {
    return selection.map((selection, i) => {
      return {
        id: String(selection.title ?? i),
        selection,
        priority: isSelected(selection, value) ? 1 : 0,
      };
    });
  }, [selection, value]);

  const isItemActive = useCallback(
    ({ selection }: SelectItem) => isSelected(selection, value),
    [value],
  );

  const hiddenWidgetAtom = useMemo(
    () =>
      isReference
        ? createWidgetAtom({
            schema: {
              ...schema,
              uid: uniqueId("w"),
              name: uniqueId("$nav"),
              hideIf: undefined,
              showIf: undefined,
              hidden: true,
            },
            formAtom,
          })
        : null,
    [isReference, formAtom, schema],
  );

  return (
    <FieldControl {...props}>
      {hiddenWidgetAtom && (
        <ManyToOne
          schema={schema}
          widgetAtom={hiddenWidgetAtom}
          valueAtom={valueAtom as ValueAtom<DataRecord>}
          formAtom={formAtom}
        />
      )}
      <OverflowList
        className={styles.container}
        items={items}
        isItemActive={isItemActive}
        onItemClick={handleChange}
        renderItem={(_props) => <Item {..._props} readonly={readonly} />}
        renderMenuTrigger={MenuTrigger}
        renderMenuItem={MenuItem}
      />
    </FieldControl>
  );
}
