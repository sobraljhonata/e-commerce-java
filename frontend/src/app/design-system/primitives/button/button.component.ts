export type ButtonVariant = 'primary' | 'secondary' | 'danger';

export interface ButtonProps {
  label: string;
  variant?: ButtonVariant;
  disabled?: boolean;
}
