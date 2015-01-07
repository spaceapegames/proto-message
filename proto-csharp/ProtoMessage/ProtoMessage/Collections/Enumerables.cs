using System;
using System.Collections;

namespace Google.ProtocolBuffers.Collections
{
  public static class Enumerables
  {
    public static bool Equals(IEnumerable left, IEnumerable right)
    {
      IEnumerator leftEnumerator = left.GetEnumerator();
      try
      {
        foreach (object rightObject in right)
        {
          if (!leftEnumerator.MoveNext())
          {
            return false;
          }
          if (!Equals(leftEnumerator.Current, rightObject))
          {
            return false;
          }
        }
        if (leftEnumerator.MoveNext())
        {
          return false;
        }
      }
      finally
      {
        IDisposable leftEnumeratorDisposable = leftEnumerator as IDisposable;
        if (leftEnumeratorDisposable != null)
        {
          leftEnumeratorDisposable.Dispose();
        }
      }
      return true;
    }
  }
}

