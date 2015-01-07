using System;
using System.Collections;
using System.Collections.Generic;

namespace Google.ProtocolBuffers.Collections
{
  public static class Dictionaries
  {
    /// <summary>
    /// Compares two dictionaries for equality. Each value is compared with equality using Equals
    /// for non-IEnumerable implementations, and using EnumerableEquals otherwise.
    /// TODO(jonskeet): This is clearly pretty slow, and involves lots of boxing/unboxing...
    /// </summary>
    public static bool Equals<TKey, TValue>(IDictionary<TKey, TValue> left, IDictionary<TKey, TValue> right)
    {
      if (left.Count != right.Count)
      {
        return false;
      }
      foreach (KeyValuePair<TKey, TValue> leftEntry in left)
      {
        TValue rightValue;
        if (!right.TryGetValue(leftEntry.Key, out rightValue))
        {
          return false;
        }

        IEnumerable leftEnumerable = leftEntry.Value as IEnumerable;
        IEnumerable rightEnumerable = rightValue as IEnumerable;
        if (leftEnumerable == null || rightEnumerable == null)
        {
          if (!Equals(leftEntry.Value, rightValue))
          {
            return false;
          }
        }
        else
        {
          if (!Enumerables.Equals(leftEnumerable, rightEnumerable))
          {
            return false;
          }
        }
      }
      return true;
    }

    public static IDictionary<TKey, TValue> AsReadOnly<TKey, TValue>(IDictionary<TKey, TValue> dictionary)
    {
      return dictionary.IsReadOnly ? dictionary : new ReadOnlyDictionary<TKey, TValue>(dictionary);
    }

    /// <summary>
    /// Creates a hashcode for a dictionary by XORing the hashcodes of all the fields
    /// and values. (By XORing, we avoid ordering issues.)
    /// TODO(jonskeet): Currently XORs other stuff too, and assumes non-null values.
    /// </summary>
    public static int GetHashCode<TKey, TValue>(IDictionary<TKey, TValue> dictionary)
    {
      int ret = 31;
      foreach (KeyValuePair<TKey, TValue> entry in dictionary)
      {
        int hash = entry.Key.GetHashCode() ^ GetDeepHashCode(entry.Value);
        ret ^= hash;
      }
      return ret;
    }

    /// <summary>
    /// Determines the hash of a value by either taking it directly or hashing all the elements
    /// for IEnumerable implementations.
    /// </summary>
    private static int GetDeepHashCode(object value)
    {
      IEnumerable iterable = value as IEnumerable;
      if (iterable == null)
      {
        return value.GetHashCode();
      }
      int hash = 29;
      foreach (object element in iterable)
      {
        hash = hash * 37 + element.GetHashCode();
      }
      return hash;
    }
  }
}

