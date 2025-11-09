import React, { useContext, useEffect } from 'react';
import SimpleTable from './SimpleTable';

export default function About() {
  const authors = [
    { id: 1, name: 'Ariel A.', role: '' },
    { id: 2, name: 'Robert D.', role: '' },
    { id: 3, name: 'Justin L.', role: '' },
    { id: 4, name: 'Karri F.', role: '' },
    { id: 5, name: 'Richard G.', role: '' },
  ];

  return (
    <div>
      <img src="chatty-catty-logo_400x400.jpg" />

      <SimpleTable data={authors} />
    </div>
  );
}