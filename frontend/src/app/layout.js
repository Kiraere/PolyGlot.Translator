export const metadata = {
  title: 'PolyGlot Translator',
  description: 'Інтелектуальний словник для вивчення мов',
};

export default function RootLayout({ children }) {
  return (
    <html lang="uk">
      <body>{children}</body>
    </html>
  );
}
