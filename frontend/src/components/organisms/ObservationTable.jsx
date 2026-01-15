export function ObservationTable({
  observations = [],
  columns = [],
  page = 0,
  totalPages = 0,
  onPageChange,
  emptyMessage = 'No observations available yet.',
  rowKey = 'id'
}) {
  const hasPagination = typeof onPageChange === 'function'
  const safeTotalPages = Math.max(totalPages, 1)
  const resolvedColumns = columns.filter(Boolean)

  return (
    <div className="observation-table">
      <style>{`
        .observation-table {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }
        .observation-table-scroll {
          overflow-x: auto;
          border-radius: 12px;
          border: 1px solid #e5e7eb;
          background: white;
        }
        .observation-table table {
          width: 100%;
          border-collapse: collapse;
          min-width: 720px;
        }
        .observation-table thead {
          background: linear-gradient(135deg, #eef2ff 0%, #e0f2fe 100%);
        }
        .observation-table th,
        .observation-table td {
          text-align: left;
          padding: 12px 14px;
          border-bottom: 1px solid #e5e7eb;
          font-size: 14px;
        }
        .observation-table th {
          font-size: 12px;
          letter-spacing: 0.04em;
          text-transform: uppercase;
          color: #475569;
        }
        .observation-table tbody tr:nth-child(even) {
          background: #f8fafc;
        }
        .observation-table tbody tr:hover {
          background: #f1f5f9;
        }
        .observation-table .cell-pill {
          font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
          background: #f1f5f9;
          padding: 4px 8px;
          border-radius: 999px;
          font-size: 12px;
          display: inline-block;
        }
        .observation-table .chip-list {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
        }
        .observation-table .chip {
          background: #ecfeff;
          color: #0f766e;
          padding: 4px 8px;
          border-radius: 999px;
          font-size: 12px;
          font-weight: 600;
          border: 1px solid #ccfbf1;
        }
        .table-empty {
          color: #6b7280;
          margin: 0;
        }
        .table-pagination {
          display: flex;
          justify-content: space-between;
          align-items: center;
          gap: 12px;
          flex-wrap: wrap;
        }
        .table-pagination button {
          padding: 8px 14px;
          border-radius: 8px;
          border: 1px solid #d1d5db;
          background: white;
          cursor: pointer;
          font-size: 14px;
        }
        .table-pagination button:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }
        @media (max-width: 640px) {
          .observation-table table {
            min-width: 600px;
          }
        }
      `}</style>

      {observations.length === 0 || resolvedColumns.length === 0 ? (
        <p className="table-empty">{emptyMessage}</p>
      ) : (
        <>
          <div className="observation-table-scroll">
            <table>
              <thead>
                <tr>
                  {resolvedColumns.map((column) => (
                    <th key={column.key || column.label}>{column.label}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {observations.map((row, index) => {
                  const key = typeof rowKey === 'function'
                    ? rowKey(row, index)
                    : row?.[rowKey] ?? index
                  return (
                    <tr key={key}>
                      {resolvedColumns.map((column) => {
                        const accessor = column.accessor
                        const value = typeof accessor === 'function'
                          ? accessor(row)
                          : accessor
                            ? row?.[accessor]
                            : undefined
                        const content = column.render
                          ? column.render(row, value, index)
                          : (value ?? '—')
                        return (
                          <td key={column.key || column.label}>
                            {content}
                          </td>
                        )
                      })}
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
          {hasPagination && (
            <div className="table-pagination">
              <span style={{ color: '#6b7280' }}>
                Page {page + 1} of {safeTotalPages}
              </span>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button
                  type="button"
                  onClick={() => onPageChange(Math.max(0, page - 1))}
                  disabled={page <= 0}
                >
                  Previous
                </button>
                <button
                  type="button"
                  onClick={() => onPageChange(Math.min(safeTotalPages - 1, page + 1))}
                  disabled={page >= safeTotalPages - 1}
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}
