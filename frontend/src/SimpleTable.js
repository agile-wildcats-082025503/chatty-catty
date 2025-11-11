import React from 'react';

export default function SimpleTable({ data }) {
  if (!data || data.length === 0) {
    return <p>No data to display.</p>;
  }

  const columns = Object.keys(data[0]);

  return (
    <table>
      <thead>
        <tr>
          {columns.map((col) => (
            <th key={col} class={`column_${col}`}>{col}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.map((row) => (
          <tr key={row.id}>
            {columns.map((col) => (
              <td key={`${row.id}-${col}`} class={`column_${col}`}>
                {row[col]}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}