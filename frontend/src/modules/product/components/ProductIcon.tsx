import { Gamepad2, Keyboard, Laptop, Mouse, Package, Smartphone } from 'lucide-react';
import React from 'preact/compat';
import { ReactNode } from 'preact/compat';
import {
  SiApple,
  SiGoogle,
  SiAmazon,
  SiSpotify,
  SiNetflix,
  SiAsus,
  SiAcer,
  SiDell,
  SiHp,
  SiLenovo,
  SiRazer,
  SiMsi,
} from 'react-icons/si';

const categoryIcons: { [key: string]: ReactNode } = {
  laptop: <Laptop />,
  smartphone: <Smartphone />,
  console: <Gamepad2 />,
  mouse: <Mouse />,
  keyboard: <Keyboard />,
};

const brandIcons: { [key: string]: ReactNode } = {
  apple: <SiApple />,
  google: <SiGoogle />,
  amazon: <SiAmazon />,
  spotify: <SiSpotify />,
  netflix: <SiNetflix />,
  asus: <SiAsus />,
  acer: <SiAcer />,
  dell: <SiDell />,
  hp: <SiHp />,
  lenovo: <SiLenovo />,
  razer: <SiRazer />,
  msi: <SiMsi />,
};

const ProductIcon = (type: 'category' | 'brand', value: string) => {
  const defaultIcon = <Package />;
  switch (type) {
    case 'category':
      return (
        <div className="w-4 h-4 inline">{categoryIcons[value.toLowerCase()] || defaultIcon}</div>
      );
    case 'brand':
      return <div className="w-4 h-4 inline">{brandIcons[value.toLowerCase()] || defaultIcon}</div>;
    default:
      return defaultIcon;
  }
};

export default ProductIcon;
