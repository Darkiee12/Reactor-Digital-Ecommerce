import {
  Computer,
  Cpu,
  Gamepad2,
  Headset,
  Keyboard,
  Laptop,
  Microchip,
  Mouse,
  Package,
  Printer,
  Router,
  Smartphone,
  Tablet,
  Watch,
  Webcam,
  Wifi,
} from 'lucide-react';

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
  SiSamsung,
  SiIntel,
  SiAmd,
  SiNvidia,
  SiLogitech,
  SiTplink,
  SiSony,
  SiMitsubishi,
  SiSharp,
  SiPanasonic,
  SiPioneerdj,
  SiToshiba,
  SiXiaomi,
  SiOneplus,
  SiHuawei,
  SiLg,
  SiNokia,
  SiOppo,
  SiPlaystation,
  SiVivo,
  SiHonor,
} from 'react-icons/si';

const categoryIcons = (category: string, size: number): ReactNode => {
  const icons: { [key: string]: ReactNode } = {
    laptop: <Laptop size={size} />,
    smartphone: <Smartphone size={size} />,
    console: <Gamepad2 size={size} />,
    mouse: <Mouse size={size} />,
    keyboard: <Keyboard size={size} />,
    tablet: <Tablet size={size} />,
    printer: <Printer size={size} />,
    webcam: <Webcam size={size} />,
    pc: <Computer size={size} />,
    headset: <Headset size={size} />,
    smartwatch: <Watch size={size} />,
    'wifi card': <Wifi size={size} />,
    router: <Router size={size} />,
    cpu: <Cpu size={size} />,
    gpu: <Microchip size={size} />,
    // 'vr headset': <RectangleGoggles size={size} />,
  };

  return icons[category.toLowerCase()] ?? <Package size={size} />;
};

const brandIcons = (brand: string, size: number): ReactNode => {
  const icon: { [key: string]: ReactNode } = {
    apple: <SiApple size={size} />,
    google: <SiGoogle size={size} />,
    amazon: <SiAmazon size={size} />,
    spotify: <SiSpotify size={size} />,
    netflix: <SiNetflix size={size} />,
    asus: <SiAsus size={size} />,
    acer: <SiAcer size={size} />,
    dell: <SiDell size={size} />,
    hp: <SiHp size={size} />,
    lenovo: <SiLenovo size={size} />,
    razer: <SiRazer size={size} />,
    msi: <SiMsi size={size} />,
    samsung: <SiSamsung size={size} />,
    intel: <SiIntel size={size} />,
    amd: <SiAmd size={size} />,
    nvidia: <SiNvidia size={size} />,
    logitech: <SiLogitech size={size} />,
    'tp-link': <SiTplink size={size} />,
    sony: <SiSony size={size} />,
    mitsubishi: <SiMitsubishi size={size} />,
    sharp: <SiSharp size={size} />,
    panasonic: <SiPanasonic size={size} />,
    pioneer: <SiPioneerdj size={size} />,
    toshiba: <SiToshiba size={size} />,
    xiaomi: <SiXiaomi size={size} />,
    oneplus: <SiOneplus size={size} />,
    huawei: <SiHuawei size={size} />,
    lg: <SiLg size={size} />,
    nokia: <SiNokia size={size} />,
    oppo: <SiOppo size={size} />,
    playstation: <SiPlaystation size={size} />,
    vivo: <SiVivo size={size} />,
    honor: <SiHonor size={size} />,
  };

  return icon[brand.toLowerCase()] ?? <Package size={size} />;
};

export const ProductIcon = (
  type: 'category' | 'brand',
  value: string,
  size: number = 4
): ReactNode => {
  const icon =
    type === 'category' ? (
      categoryIcons(value, size)
    ) : type === 'brand' ? (
      brandIcons(value, size)
    ) : (
      <Package size={size} />
    );

  return <div className="inline">{icon}</div>;
};

export default ProductIcon;
